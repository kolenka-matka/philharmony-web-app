package com.example.demo.repositories;

import com.example.demo.models.entities.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, String> {
    Optional<Hall> findByName(String name);
    List<Hall> findByAddressContainingIgnoreCase(String address);
    boolean existsByName(String name);

    // Залы с вместимостью больше указанной
    List<Hall> findByCapacityGreaterThan(Integer minCapacity);
}