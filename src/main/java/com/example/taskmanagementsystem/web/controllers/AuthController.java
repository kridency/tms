package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.securities.SecurityService;
import com.example.taskmanagementsystem.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SecurityService securityService;

    @Operation(summary = "Аутентифицировать пользователя",
            description = "Аутентифицирует зарегистрированного пользователя.")
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(loginRequest));
    }

    @Operation(summary = "Зарегистрировать пользователя",
            description = "Регистрирует нового пользователя.")
    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        securityService.register(createUserRequest);

        return ResponseEntity.ok(new SimpleResponse("User created!"));
    }

    @Operation(summary = "Обновить жетон пользователя",
            description = "Обновляет жетон аутентификации пользователя.")
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refreshToken(request));
    }

    @Operation(summary = "Выход пользователя",
            description = "Выход пользователя.")
    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        securityService.logout();
        return ResponseEntity.ok(new SimpleResponse("User logout. Email is: " + userDetails.getUsername()));
    }
}
