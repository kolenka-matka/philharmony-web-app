package com.example.demo.services;

import com.example.demo.dto.BookingCreateDto;
import com.example.demo.dto.BookingViewDto;
import com.example.demo.models.entities.Booking;
import com.example.demo.models.entities.Event;
import com.example.demo.models.entities.User;
import com.example.demo.models.exceptions.BookingNotFoundException;
import com.example.demo.models.exceptions.EventNotFoundException;
import com.example.demo.repositories.BookingRepository;
import com.example.demo.repositories.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final AuthService authService;
    private final ModelMapper mapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              EventRepository eventRepository,
                              AuthService authService,
                              ModelMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.authService = authService;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void createBooking(String eventTitle,
                              BookingCreateDto bookingCreateDto,
                              Principal principal) {

        User user = getCurrentUser(principal);

        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Мероприятие '" + eventTitle + "' не найдено"
                        )
                );

        if (bookingCreateDto.getSeatsCount() > event.getAvailableSeats()) {
            throw new IllegalArgumentException("Недостаточно свободных мест");
        }

        if (bookingCreateDto.getSeatsCount() > 10) {
            throw new IllegalArgumentException(
                    "Максимальное количество мест для одного бронирования — 10"
            );
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setSeatsCount(bookingCreateDto.getSeatsCount());
        booking.setComment(
                bookingCreateDto.getComment() == null ||
                        bookingCreateDto.getComment().isBlank()
                        ? "Без комментария"
                        : bookingCreateDto.getComment().trim()
        );

        event.setAvailableSeats(
                event.getAvailableSeats() - bookingCreateDto.getSeatsCount()
        );

        bookingRepository.save(booking);
        eventRepository.save(event);
    }

    @Override
    public List<BookingViewDto> getUserBookings(Principal principal) {
        User user = getCurrentUser(principal);

        return bookingRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toBookingViewDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingViewDto getBookingById(String id, Principal principal) {
        User user = getCurrentUser(principal);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Бронирование с ID '" + id + "' не найдено"
                        )
                );

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Доступ запрещен");
        }

        return toBookingViewDto(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(String bookingId, Principal principal) {
        User user = getCurrentUser(principal);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Бронирование с ID '" + bookingId + "' не найдено"
                        )
                );

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "Вы не можете отменить чужое бронирование"
            );
        }

        Event event = booking.getEvent();
        event.setAvailableSeats(
                event.getAvailableSeats() + booking.getSeatsCount()
        );

        bookingRepository.delete(booking);
        eventRepository.save(event);
    }

    private User getCurrentUser(Principal principal) {
        return authService.getUser(principal.getName());
    }

    private BookingViewDto toBookingViewDto(Booking booking) {
        BookingViewDto dto = mapper.map(booking, BookingViewDto.class);

        dto.setEventTitle(booking.getEvent().getTitle());
        dto.setEventDateTime(booking.getEvent().getDateTime());
        dto.setHallName(booking.getEvent().getHall().getName());

        dto.setUserFullName(booking.getUser().getFullName());
        dto.setUserEmail(booking.getUser().getEmail());

        if (dto.getComment() == null || dto.getComment().isBlank()) {
            dto.setComment("Без комментария");
        }

        return dto;
    }
}