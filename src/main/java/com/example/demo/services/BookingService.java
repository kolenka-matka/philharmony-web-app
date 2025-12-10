package com.example.demo.services;

import com.example.demo.dto.BookingCreateDto;
import com.example.demo.dto.BookingViewDto;
import com.example.demo.models.entities.Booking;
import com.example.demo.models.entities.Event;
import com.example.demo.models.entities.User;
import com.example.demo.models.exceptions.BookingNotFoundException;
import com.example.demo.models.exceptions.EventNotFoundException;
import com.example.demo.models.exceptions.UserNotFoundException;
import com.example.demo.repositories.BookingRepository;
import com.example.demo.repositories.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final AuthService authService;

    public BookingService(BookingRepository bookingRepository, EventRepository eventRepository, AuthService authService) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.authService = authService;
    }

    @Transactional
    public void createBooking(String eventTitle, BookingCreateDto bookingCreateDto, Principal principal) {
        log.info("Создание бронирования для мероприятия {} пользователем {}",
                eventTitle, principal.getName());

        User user;
        try {
            user = authService.getUser(principal.getName());
        } catch (Exception e) {
            throw new UserNotFoundException("Пользователь '" + principal.getName() + "' не найден");
        }

        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие '" + eventTitle + "' не найдено"));

        if (event.getAvailableSeats() < bookingCreateDto.getSeatsCount()) {
            throw new IllegalArgumentException("Недостаточно свободных мест. Доступно: " + event.getAvailableSeats());
        }

        if (bookingCreateDto.getSeatsCount() > 10) {
            throw new IllegalArgumentException("Максимальное количество мест для одного бронирования - 10");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);

        if (bookingCreateDto.getComment() == null || bookingCreateDto.getComment().trim().isEmpty()) {
            booking.setComment("Без комментария");
        } else {
            booking.setComment(bookingCreateDto.getComment().trim());
        }

        booking.setSeatsCount(bookingCreateDto.getSeatsCount());

        event.setAvailableSeats(event.getAvailableSeats() - bookingCreateDto.getSeatsCount());

        bookingRepository.save(booking);
        eventRepository.save(event);

        log.info("Бронирование создано успешно. ID: {}, мест: {}",
                booking.getId(), booking.getSeatsCount());
    }

    @Transactional(readOnly = true)
    public List<BookingViewDto> getUserBookings(Principal principal) {
        User user;
        try {
            user = authService.getUser(principal.getName());
        } catch (Exception e) {
            throw new UserNotFoundException("Пользователь '" + principal.getName() + "' не найден");
        }

        return bookingRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToBookingViewDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingViewDto getBookingById(String id, Principal principal) {
        User user;
        try {
            user = authService.getUser(principal.getName());
        } catch (Exception e) {
            throw new UserNotFoundException("Пользователь '" + principal.getName() + "' не найден");
        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID '" + id + "' не найдено"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Доступ запрещен");
        }

        return mapToBookingViewDto(booking);
    }

    private BookingViewDto mapToBookingViewDto(Booking booking) {
        return new BookingViewDto(
                booking.getId(),
                booking.getEvent().getTitle(),
                booking.getEvent().getDateTime(),
                booking.getEvent().getHall().getName(),
                booking.getComment(),
                booking.getSeatsCount(),
                booking.getCreatedAt(),
                booking.getUser().getFullName(),
                booking.getUser().getEmail()
        );
    }

    @Transactional
    public void cancelBooking(String bookingId, Principal principal) {
        log.info("Отмена бронирования {} пользователем {}", bookingId, principal.getName());

        User user;
        try {
            user = authService.getUser(principal.getName());
        } catch (Exception e) {
            throw new UserNotFoundException("Пользователь '" + principal.getName() + "' не найден");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID '" + bookingId + "' не найдено"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Вы не можете отменить чужое бронирование");
        }

        Event event = booking.getEvent();

        event.setAvailableSeats(event.getAvailableSeats() + booking.getSeatsCount());

        bookingRepository.delete(booking);
        eventRepository.save(event);

        log.info("Бронирование отменено успешно");
    }
}