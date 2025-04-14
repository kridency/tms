package com.example.taskmanagementsystem.dto;

import com.example.taskmanagementsystem.entities.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ по запросу на регистрацию/обновление учетных данных пользователя.")
public class UserDto {
    @Schema(description = "Адрес электронной почты пользователя.")
    private String email;
    @Schema(description = "Пароль пользователя.")
    private String password;
    @Schema(description = "Перечень ролей пользователя пользователя.")
    private Set<RoleType> roles;
}
