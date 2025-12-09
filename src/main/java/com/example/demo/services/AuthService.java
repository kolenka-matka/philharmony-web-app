package com.example.demo.services;

import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.models.entities.User;

public interface AuthService {
    void register(UserRegistrationDto registrationDTO);

    User getUser(String username);
}
