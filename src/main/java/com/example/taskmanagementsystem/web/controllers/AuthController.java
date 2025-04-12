package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.dto.RefreshTokenDto;
import com.example.taskmanagementsystem.securities.UserService;
import com.example.taskmanagementsystem.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Аутентифицировать пользователя",
            description = "Аутентифицирует зарегистрированного пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public RefreshTokenDto authUser(@RequestBody AuthRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        return userService.issueToken(request);
    }

    @Operation(summary = "Зарегистрировать пользователя",
            description = "Регистрирует нового пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/register")
    public SimpleResponse registerUser(@RequestBody @Valid AuthRequest request) {
        userService.register(request);
        return new SimpleResponse("User created!");
    }

    @Operation(summary = "Обновить жетон пользователя",
            description = "Обновляет жетон аутентификации пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh-token")
    public RefreshTokenDto refreshToken(@RequestBody RefreshTokenRequest request) {
        return userService.refreshToken(request);
    }
}
