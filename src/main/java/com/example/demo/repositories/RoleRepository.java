package com.example.demo.repositories;

import com.example.demo.models.entities.Role;
import com.example.demo.models.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(UserRoles name);
}