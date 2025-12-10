package com.example.demo.services;

import com.example.demo.dto.AddEventDto;
import com.example.demo.dto.ShowEventInfoDto;
import com.example.demo.dto.ShowDetailedEventInfoDto;
import com.example.demo.dto.TopEventDto;
import com.example.demo.models.entities.Event;
import com.example.demo.models.entities.Genre;
import com.example.demo.models.entities.Hall;
import com.example.demo.models.enums.EventType;
import com.example.demo.models.exceptions.EventNotFoundException;
import com.example.demo.repositories.EventRepository;
import com.example.demo.repositories.GenreRepository;
import com.example.demo.repositories.HallRepository;
import com.example.demo.repositories.specifications.EventSpecification;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final HallRepository hallRepository;
    private final GenreRepository genreRepository;
    private final ModelMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    public EventServiceImpl(EventRepository eventRepository,
                            HallRepository hallRepository,
                            GenreRepository genreRepository,
                            ModelMapper mapper) {
        this.eventRepository = eventRepository;
        this.hallRepository = hallRepository;
        this.genreRepository = genreRepository;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(value = "events", key = "'all'", unless = "#result.isEmpty()")
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
    @Cacheable(value = "events", key = "'type_' + #type")
    public List<ShowEventInfoDto> findByEventType(EventType type) {
        return eventRepository.findByEventType(type)
                .stream()
                .map(this::toShowEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowEventInfoDto> findByGenreId(String genreId) {
        Specification<Event> spec = EventSpecification.hasGenreId(genreId);
        return eventRepository.findAll(spec)
                .stream()
                .map(this::toShowEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "events", key = "'genre_' + #genreName")
    public List<ShowEventInfoDto> findByGenreName(String genreName) {
        Specification<Event> spec = EventSpecification.hasGenreName(genreName);
        return eventRepository.findAll(spec)
                .stream()
                .map(this::toShowEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "event", key = "#eventTitle", unless = "#result == null")
    public ShowDetailedEventInfoDto eventDetails(String eventTitle) {
        log.debug("Получение деталей мероприятия: {}", eventTitle);
        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие '" + eventTitle + "' не найдено"));

        return toShowDetailedEventInfoDto(event);
    }

    @Override
    public List<ShowEventInfoDto> findEventsWithFilters(String search, EventType type, String genreName) {
        Specification<Event> spec = Specification.where(EventSpecification.hasTitleContaining(search))
                .and(EventSpecification.hasEventType(type))
                .and(EventSpecification.hasGenreName(genreName));

        return eventRepository.findAll(spec)
                .stream()
                .map(this::toShowEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShowEventInfoDto> findEventsWithFiltersPaginated(String search, EventType type, String genreName, Pageable pageable) {
        Specification<Event> spec = Specification.where(EventSpecification.hasTitleContaining(search))
                .and(EventSpecification.hasEventType(type))
                .and(EventSpecification.hasGenreName(genreName));

        return eventRepository.findAll(spec, pageable)
                .map(this::toShowEventInfoDto);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "event", allEntries = true)
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

        if (dto.getGenreId() != null && !dto.getGenreId().isEmpty()) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new IllegalArgumentException("Жанр не найден"));
            event.setGenre(genre);
        }

        eventRepository.save(event);
        log.info("Мероприятие '{}' добавлено", dto.getTitle());
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "event", allEntries = true)
    public void deleteEvent(String eventTitle) {
        Event event = eventRepository.findByTitle(eventTitle)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие '" + eventTitle + "' не найдено"));

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

        if (event.getGenre() != null) {
            dto.setGenreName(event.getGenre().getName());
        } else {
            dto.setGenreName("Не указан");
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

        if (event.getGenre() != null) {
            dto.setGenreName(event.getGenre().getName());
        } else {
            dto.setGenreName("Не указан");
        }

        return dto;
    }

    @Override
    public List<TopEventDto> getTopEventsByBookings(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = eventRepository.findTopEventsByBookings(pageable);

        List<TopEventDto> topEvents = new ArrayList<>();
        int position = 1;

        for (Object[] result : results) {
            Event event = (Event) result[0];
            Long totalSeats = (Long) result[1];

            TopEventDto dto = new TopEventDto(
                    event.getTitle(),
                    totalSeats != null ? totalSeats.intValue() : 0,
                    event.getImageUrl() != null ? event.getImageUrl() : "/images/default-event.jpg",
                    position
            );

            topEvents.add(dto);
            position++;
        }

        return topEvents;
    }
}