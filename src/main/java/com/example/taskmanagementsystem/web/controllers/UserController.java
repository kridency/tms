package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.dto.UserDto;
import com.example.taskmanagementsystem.securities.UserService;
import com.example.taskmanagementsystem.web.models.AuthRequest;
import com.example.taskmanagementsystem.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Зарегистрировать пользователя",
            description = "Регистрирует нового пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public MessageDto registerUser(@RequestBody @Valid AuthRequest request) {
        userService.create(request);
        return new MessageDto("User created!");
    }

    @Operation(summary = "Обновить учетные дынные пользователя",
            description = "Обновляет адрес электронной почты, пароль и перечень ролей пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public UserDto updateUser(@RequestBody @Valid AuthRequest request, @AuthenticationPrincipal String username) {
        return userService.update(request, username);
    }

    @Operation(summary = "Удалить учетные данные пользователя",
            description = "Удаляет учетные данные пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public UserDto deleteUser(@AuthenticationPrincipal String username) {
        return userService.delete(username);
    }
}
