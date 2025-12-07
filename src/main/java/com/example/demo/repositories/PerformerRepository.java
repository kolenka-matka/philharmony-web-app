package com.example.demo.repositories;

import com.example.demo.models.entities.Performer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformerRepository extends JpaRepository<Performer, String> {
    Optional<Performer> findByName(String name);
    List<Performer> findByNameContainingIgnoreCase(String name);
    List<Performer> findByGenreId(String genreId);
    boolean existsByName(String name);

    // Исполнители определённого жанра
    @Query("SELECT p FROM Performer p WHERE p.genre.name = :genreName")
    List<Performer> findByGenreName(@Param("genreName") String genreName);
}