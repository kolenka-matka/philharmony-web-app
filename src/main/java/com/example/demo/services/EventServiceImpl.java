package com.example.demo.services;

import com.example.demo.controllers.EventController;
import com.example.demo.dto.AddEventDto;
import com.example.demo.dto.ShowEventInfoDto;
import com.example.demo.dto.ShowDetailedEventInfoDto;
import com.example.demo.models.entities.Event;
import com.example.demo.models.entities.Hall;
import com.example.demo.repositories.EventRepository;
import com.example.demo.repositories.HallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final HallRepository hallRepository;
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    public EventServiceImpl(EventRepository eventRepository, HallRepository hallRepository) {
        this.eventRepository = eventRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public List<ShowEventInfoDto> allEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(event -> {
            ShowEventInfoDto dto = new ShowEventInfoDto();
            dto.setTitle(event.getTitle());
            dto.setDateTime(event.getDateTime());
            dto.setAvailableSeats(event.getAvailableSeats());
            dto.setImageUrl(event.getImageUrl());

            if (event.getHall() != null) {
                dto.setHallName(event.getHall().getName());
            } else {
                dto.setHallName("Не указан");
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<ShowEventInfoDto> allEventsPaginated(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(event -> {
                    ShowEventInfoDto dto = new ShowEventInfoDto();
                    dto.setTitle(event.getTitle());
                    dto.setDateTime(event.getDateTime());
                    dto.setAvailableSeats(event.getAvailableSeats());
                    dto.setImageUrl(event.getImageUrl());

                    if (event.getHall() != null) {
                        dto.setHallName(event.getHall().getName());
                    } else {
                        dto.setHallName("Не указан");
                    }
                    return dto;
                });
    }

    @Override
    public List<ShowEventInfoDto> searchEvents(String search) {
        return eventRepository.findByTitleContainingIgnoreCase(search).stream()
                .map(event -> {
                    ShowEventInfoDto dto = new ShowEventInfoDto();
                    dto.setTitle(event.getTitle());
                    dto.setDateTime(event.getDateTime());
                    dto.setAvailableSeats(event.getAvailableSeats());
                    dto.setImageUrl(event.getImageUrl());

                    if (event.getHall() != null) {
                        dto.setHallName(event.getHall().getName());
                    } else {
                        dto.setHallName("Не указан");
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ShowDetailedEventInfoDto eventDetails(String eventTitle) {
        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() -> new IllegalArgumentException("Мероприятие не найдено: " + eventTitle));

        ShowDetailedEventInfoDto dto = new ShowDetailedEventInfoDto();
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDateTime(event.getDateTime());
        dto.setAvailableSeats(event.getAvailableSeats());
        dto.setImageUrl(event.getImageUrl());

        if (event.getHall() != null) {
            dto.setHallName(event.getHall().getName());
            dto.setHallAddress(event.getHall().getAddress());
            dto.setCapacity(event.getHall().getCapacity());
        } else {
            dto.setHallName("Не указан");
            dto.setHallAddress("Не указан");
            dto.setCapacity(0);
        }

        return dto;
    }

    @Override
    @Transactional
    public void addEvent(AddEventDto dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDateTime(dto.getDateTime());
        event.setEventType(dto.getEventType());
        event.setImageUrl(dto.getImageUrl());
        event.setAvailableSeats(dto.getAvailableSeats());

        // Сохраняем зал
        Hall hall = hallRepository.findById(String.valueOf(dto.getHallId()))
                .orElseThrow(() -> new IllegalArgumentException("Зал не найден"));

        event.setHall(hall);

        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void deleteEvent(String eventTitle) {
        log.debug("Удаление мероприятия: {}", eventTitle);

        // Найти мероприятие
        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() -> new IllegalArgumentException("Мероприятие не найдено"));

        try {
            eventRepository.delete(event);
            log.info("Мероприятие успешно удалено: {}", eventTitle);
        } catch (DataIntegrityViolationException e) {
            log.warn("Нельзя удалить мероприятие '{}'. Есть связанные бронирования", eventTitle);
            throw new IllegalArgumentException(
                    "Нельзя удалить мероприятие '" + eventTitle +
                            "'. Существуют связанные бронирования. Сначала удалите все бронирования."
            );
        }
    }

}