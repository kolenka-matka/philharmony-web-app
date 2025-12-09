package com.example.demo.repositories;

import com.example.demo.models.entities.Booking;
import com.example.demo.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByUserOrderByCreatedAtDesc(User user);

    List<Booking> findByEventId(String eventId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.id = :eventId")
    Long countByEventId(@Param("eventId") String eventId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> findByUserId(@Param("userId") String userId);
}