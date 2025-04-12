package com.example.taskmanagementsystem.dto;

import com.example.taskmanagementsystem.entities.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Email(message = "Не")
    private String email;
    @NotNull(message = "Не указано значение для поля task.")
    private String password;
    private Set<RoleType> roles;
}
