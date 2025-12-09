package com.example.demo.repositories;

import com.example.demo.models.entities.Event;
import com.example.demo.models.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String>, JpaSpecificationExecutor<Event> {

    // Старые методы остаются для совместимости
    List<Event> findByDateTimeAfter(LocalDateTime dateTime);
    List<Event> findByEventTypeAndDateTimeAfter(EventType eventType, LocalDateTime dateTime);
    List<Event> findByEventType(EventType eventType);
    List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByEventTypeAndTitleContainingIgnoreCase(EventType eventType, String title);
    List<Event> findByDateTimeBefore(LocalDateTime dateTime);
    List<Event> findByAvailableSeatsGreaterThan(Integer minSeats);
    Optional<Event> findByTitle(String title);
    boolean existsByTitle(String title);
    void deleteByTitle(String title);

    // Метод для комбинированных фильтров через спецификации
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);
    List<Event> findAll(Specification<Event> spec);

    @Query("SELECT e, COALESCE(SUM(b.seatsCount), 0) as totalSeats " +
            "FROM Event e " +
            "LEFT JOIN Booking b ON e.id = b.event.id " +
            "GROUP BY e.id " +
            "ORDER BY totalSeats DESC")
    List<Object[]> findTopEventsByBookings(Pageable pageable);
}