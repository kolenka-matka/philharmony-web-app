package com.example.demo.repositories;

import com.example.demo.models.entities.Event;
import com.example.demo.models.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    // Будущие мероприятия
    List<Event> findByDateTimeAfter(LocalDateTime dateTime);

    // Будущие мероприятия определенного типа
    List<Event> findByEventTypeAndDateTimeAfter(EventType eventType, LocalDateTime dateTime);

    List<Event> findByEventType(EventType eventType);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByEventTypeAndTitleContainingIgnoreCase(EventType eventType, String title);

    // Прошедшие мероприятия
    List<Event> findByDateTimeBefore(LocalDateTime dateTime);

    // Мероприятия с достаточным количеством мест
    List<Event> findByAvailableSeatsGreaterThan(Integer minSeats);

    Optional<Event> findByTitle(String title);

    boolean existsByTitle(String title);

    void deleteByTitle(String title);
}