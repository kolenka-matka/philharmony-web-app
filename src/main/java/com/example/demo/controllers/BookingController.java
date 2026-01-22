package com.example.demo.controllers;

import com.example.demo.dto.BookingCreateDto;
import com.example.demo.services.AuthService;
import com.example.demo.services.BookingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    public BookingController(BookingService bookingService, AuthService authService) {
        this.bookingService = bookingService;
        this.authService = authService;
    }

    @ModelAttribute("bookingCreateDto")
    public BookingCreateDto initForm(Principal principal) {
        BookingCreateDto dto = new BookingCreateDto();

        if (principal != null) {
            try {
                var user = authService.getUser(principal.getName());
                dto.setUserFullName(user.getFullName());
                dto.setUserEmail(user.getEmail());
            } catch (Exception e) {
                log.warn("Не удалось получить данные пользователя: {}", e.getMessage());
            }
        }

        return dto;
    }

    @GetMapping("/create/{title}")
    @PreAuthorize("isAuthenticated()")
    public String showBookingForm(@PathVariable String title,
                                  Model model,
                                  Principal principal) {
        log.debug("Отображение формы бронирования для мероприятия: {}", title);

        try {
            var user = authService.getUser(principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("title", title);

            if (!model.containsAttribute("bookingCreateDto")) {
                BookingCreateDto dto = new BookingCreateDto();
                dto.setUserFullName(user.getFullName());
                dto.setUserEmail(user.getEmail());
                model.addAttribute("bookingCreateDto", dto);
            }

        } catch (Exception e) {
            log.error("Ошибка при получении данных пользователя: ", e);
            model.addAttribute("errorMessage", "Не удалось загрузить данные пользователя");
        }

        return "booking-create";
    }

    @PostMapping("/create/{title}")
    @PreAuthorize("isAuthenticated()")
    public String createBooking(@PathVariable String title,
                                @Valid @ModelAttribute BookingCreateDto bookingCreateDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {
        log.debug("Обработка бронирования для мероприятия: {}", title);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при бронировании: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("bookingCreateDto", bookingCreateDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookingCreateDto", bindingResult);

            return "redirect:/bookings/create/" + title;
        }

        try {
            bookingService.createBooking(title, bookingCreateDto, principal);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Бронирование успешно создано! Билеты зарезервированы.");
            log.info("Бронирование успешно создано пользователем {} для мероприятия {}",
                    principal.getName(), title);
            return "redirect:/users/profile";

        } catch (Exception e) {
            log.error("Ошибка при создании бронирования: ", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("bookingCreateDto", bookingCreateDto);
            return "redirect:/bookings/create/" + title;
        }
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public String myBookings(Model model, Principal principal) {
        log.debug("Отображение бронирований пользователя: {}", principal.getName());

        try {
            var bookings = bookingService.getUserBookings(principal);
            model.addAttribute("bookings", bookings);
        } catch (Exception e) {
            log.error("Ошибка при получении бронирований: ", e);
            model.addAttribute("errorMessage", "Не удалось загрузить бронирования");
        }

        return "booking-list";
    }

    @PostMapping("/cancel/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public String cancelBooking(@PathVariable String bookingId,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {
        log.debug("Отмена бронирования: {} пользователем: {}", bookingId, principal.getName());

        try {
            bookingService.cancelBooking(bookingId, principal);
            redirectAttributes.addFlashAttribute("successMessage", "Бронирование отменено успешно!");
        } catch (Exception e) {
            log.error("Ошибка при отмене бронирования: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        return "redirect:/bookings/my";
    }
}