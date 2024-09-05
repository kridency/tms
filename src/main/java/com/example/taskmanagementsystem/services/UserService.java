package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.entities.User;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с Email = " + email + " не найден."));
    }
}
