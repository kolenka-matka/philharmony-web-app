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
public interface EventRepository extends JpaRepository<Event, String>,
        JpaSpecificationExecutor<Event> {

    List<Event> findByEventType(EventType eventType);
    List<Event> findByTitleContainingIgnoreCase(String title);
    Optional<Event> findByTitle(String title);
    boolean existsByTitle(String title);
    void deleteByTitle(String title);

    // методы спецификаций
    @Override
    List<Event> findAll(Specification<Event> spec);
    @Override
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    // аналитика
    @Query("""
        SELECT e, COALESCE(SUM(b.seatsCount), 0)
        FROM Event e
        LEFT JOIN Booking b ON e.id = b.event.id
        GROUP BY e.id
        ORDER BY COALESCE(SUM(b.seatsCount), 0) DESC
    """)
    List<Object[]> findTopEventsByBookings(Pageable pageable);
}