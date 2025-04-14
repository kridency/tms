package com.example.taskmanagementsystem.web.models;

import com.example.taskmanagementsystem.entities.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на создание/обновление учетных данных пользователя.")
public class AuthRequest {
    @NotNull(message = "Не указано значение для поля email.")
    @Email(message = "Недопустимое значение для адреса электронной почты.")
    @Schema(description = "Адрес электронной почты пользователя.")
    private String email;
    @NotNull(message = "Не указано значение для поля password.")
    @Schema(description = "Пароль пользователя.")
    private String password;
    @Schema(description = "Перечень ролей пользователя пользователя.")
    private Set<RoleType> roles;
}
