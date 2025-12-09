package com.example.demo.services;

import com.example.demo.dto.AddEventDto;
import com.example.demo.dto.ShowEventInfoDto;
import com.example.demo.dto.ShowDetailedEventInfoDto;
import com.example.demo.dto.TopEventDto;
import com.example.demo.models.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    List<ShowEventInfoDto> allEvents();
    Page<ShowEventInfoDto> allEventsPaginated(Pageable pageable);
    List<ShowEventInfoDto> searchEvents(String search);
    List<ShowEventInfoDto> findByEventType(EventType type);
    List<ShowEventInfoDto> findByGenreId(String genreId);
    List<ShowEventInfoDto> findByGenreName(String genreName);
    ShowDetailedEventInfoDto eventDetails(String eventTitle);

    // НОВЫЕ МЕТОДЫ ДЛЯ ФИЛЬТРАЦИИ
    List<ShowEventInfoDto> findEventsWithFilters(String search, EventType type, String genreName);
    Page<ShowEventInfoDto> findEventsWithFiltersPaginated(String search, EventType type, String genreName, Pageable pageable);

    void addEvent(AddEventDto dto);
    void deleteEvent(String eventTitle);

    List<TopEventDto> getTopEventsByBookings(int limit);
}