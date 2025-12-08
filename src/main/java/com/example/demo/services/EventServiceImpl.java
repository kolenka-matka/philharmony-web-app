package com.example.demo.services;

import com.example.demo.dto.AddEventDto;
import com.example.demo.dto.ShowEventInfoDto;
import com.example.demo.dto.ShowDetailedEventInfoDto;
import com.example.demo.models.entities.Event;
import com.example.demo.models.entities.Hall;
import com.example.demo.models.enums.EventType;
import com.example.demo.repositories.EventRepository;
import com.example.demo.repositories.HallRepository;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    public EventServiceImpl(EventRepository eventRepository,
                            HallRepository hallRepository,
                            ModelMapper mapper) {
        this.eventRepository = eventRepository;
        this.hallRepository = hallRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ShowEventInfoDto> allEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::toShowEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShowEventInfoDto> allEventsPaginated(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::toShowEventInfoDto);
    }

    @Override
    public List<ShowEventInfoDto> searchEvents(String search) {
        return eventRepository.findByTitleContainingIgnoreCase(search)
                .stream()
                .map(this::toShowEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowEventInfoDto> findByEventType(EventType type) {
        return eventRepository.findByEventType(type)
                .stream()
                .map(this::toShowEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShowDetailedEventInfoDto eventDetails(String eventTitle) {
        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() -> new IllegalArgumentException("Мероприятие не найдено: " + eventTitle));

        return toShowDetailedEventInfoDto(event);
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

        Hall hall = hallRepository.findById(String.valueOf(dto.getHallId()))
                .orElseThrow(() -> new IllegalArgumentException("Зал не найден"));

        event.setHall(hall);

        eventRepository.save(event);
        log.info("Мероприятие '{}' добавлено", dto.getTitle());
    }

    @Override
    @Transactional
    public void deleteEvent(String eventTitle) {
        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() -> new IllegalArgumentException("Мероприятие не найдено"));

        try {
            eventRepository.delete(event);
            log.info("Мероприятие '{}' удалено", eventTitle);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Нельзя удалить мероприятие '" + eventTitle +
                            "'. Есть связанные бронирования. Сначала удалите их."
            );
        }
    }

    private ShowEventInfoDto toShowEventInfoDto(Event event) {
        ShowEventInfoDto dto = mapper.map(event, ShowEventInfoDto.class);
        if (event.getHall() != null) {
            dto.setHallName(event.getHall().getName());
        } else {
            dto.setHallName("Не указан");
        }

        return dto;
    }

    private ShowDetailedEventInfoDto toShowDetailedEventInfoDto(Event event) {
        ShowDetailedEventInfoDto dto = mapper.map(event, ShowDetailedEventInfoDto.class);
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
}
