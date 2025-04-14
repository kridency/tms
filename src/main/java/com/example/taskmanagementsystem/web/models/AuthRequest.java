package com.example.taskmanagementsystem.web.models;

import com.example.taskmanagementsystem.entities.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
    @NotNull(message = "Не указано значение для поля email.")
    @Email(message = "Недопустимое значение для адреса электронной почты.")
    private String email;
    @NotNull(message = "Не указано значение для поля password.")
    private String password;
    private Set<RoleType> roles;
}
