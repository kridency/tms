package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.dto.TokenDto;
import com.example.taskmanagementsystem.securities.TokenService;
import com.example.taskmanagementsystem.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Аутентифицировать пользователя",
            description = "Аутентифицирует зарегистрированного пользователя и возвращает его электронный пропуск.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public TokenDto issueToken(@RequestBody @Valid AuthRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        return tokenService.create(request.getEmail());
    }

    @Operation(summary = "Обновить электронный пропуск аутентифицированного пользователя",
            description = "Обновляет электронный пропуск аутентифицированного пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public TokenDto updateToken(@AuthenticationPrincipal String username) {
        return tokenService.update(username);
    }
}
