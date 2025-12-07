package com.example.demo.controllers;

import com.example.demo.dto.AddEventDto;
import com.example.demo.dto.ShowEventInfoDto;
import com.example.demo.models.entities.Hall;
import com.example.demo.models.enums.EventType;
import com.example.demo.services.EventService;
import com.example.demo.services.HallService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final HallService hallService;

    public EventController(EventService eventService, HallService hallService) {
        this.eventService = eventService;
        this.hallService = hallService;
    }

    @GetMapping("/add")
    public String addEvent(Model model) {
        log.debug("Отображение формы добавления мероприятия");

        model.addAttribute("availableHalls", hallService.getAllHalls());
        model.addAttribute("eventTypes", EventType.values());

        return "event-add";
    }

    @ModelAttribute("eventModel")
    public AddEventDto initEvent() {
        return new AddEventDto();
    }

    @PostMapping("/add")
    public String addEvent(@Valid AddEventDto eventModel,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        log.debug("Обработка POST запроса на добавление мероприятия");

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при добавлении мероприятия: {}", bindingResult.getAllErrors());

            redirectAttributes.addFlashAttribute("availableHalls", hallService.getAllHalls());
            redirectAttributes.addFlashAttribute("eventTypes", EventType.values());
            redirectAttributes.addFlashAttribute("eventModel", eventModel);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.eventModel",
                    bindingResult);

            return "redirect:/events/add";
        }

        try {
            eventService.addEvent(eventModel);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Мероприятие '" + eventModel.getTitle() + "' успешно добавлено!");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при добавлении мероприятия: {}", e.getMessage());

            redirectAttributes.addFlashAttribute("availableHalls", hallService.getAllHalls());
            redirectAttributes.addFlashAttribute("eventTypes", EventType.values());
            redirectAttributes.addFlashAttribute("eventModel", eventModel);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/events/add";
        }

        return "redirect:/events/all";
    }

    @GetMapping("/all")
    public String showAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(required = false) String search,
            Model model) {

        log.debug("Отображение списка мероприятий: страница={}, размер={}, сортировка={}, поиск={}",
                page, size, sortBy, search);

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("eventInfos", eventService.searchEvents(search));
            model.addAttribute("search", search);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
            Page<ShowEventInfoDto> eventPage = eventService.allEventsPaginated(pageable);

            model.addAttribute("eventInfos", eventPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", eventPage.getTotalPages());
            model.addAttribute("totalItems", eventPage.getTotalElements());
        }

        return "event-all";
    }

    @GetMapping("/event-details/{event-title}")
    public String eventDetails(@PathVariable("event-title") String eventTitle, Model model) {
        log.debug("Просмотр деталей мероприятия: {}", eventTitle);
        model.addAttribute("eventDetails", eventService.eventDetails(eventTitle));
        return "event-details";
    }

    @GetMapping("/event-delete/{event-title}")
    public String deleteEvent(@PathVariable("event-title") String eventTitle,
                              RedirectAttributes redirectAttributes) {

        log.debug("Запрос на удаление мероприятия: {}", eventTitle);

        try {
            eventService.deleteEvent(eventTitle);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Мероприятие '" + eventTitle + "' успешно удалено!");
        } catch (DataIntegrityViolationException e) {
            log.warn("Нельзя удалить мероприятие '{}' из-за связанных данных: {}", eventTitle, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Нельзя удалить мероприятие '" + eventTitle +
                            "'. Существуют связанные бронирования. Сначала удалите все бронирования.");
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при удалении мероприятия '{}': {}", eventTitle, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/events/all";
    }
}