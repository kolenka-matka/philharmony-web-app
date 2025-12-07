package com.example.demo.repositories;

import com.example.demo.models.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // Все бронирования пользователя (для личного кабинета)
    List<Booking> findByUserId(String userId);

    // Все бронирования на конкретный концерт (для администратора)
    List<Booking> findByEventId(String eventId);

    // Конкретное бронирование пользователя на концерт
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.event.id = :eventId")
    List<Booking> findByUserAndEvent(@Param("userId") String userId,
                                       @Param("eventId") String eventId);

    // Подсчёт количества бронирований на концерт
    long countByEventId(String eventId);

    // Проверка, бронировал ли пользователь уже этот концерт
    boolean existsByUserIdAndEventId(String userId, String eventId);
}