package com.example.taskmanagementsystem.web.models;

import com.example.taskmanagementsystem.entities.PriorityType;
import com.example.taskmanagementsystem.entities.StatusType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest {
    @NotNull(message = "Не указано значение поля title.")
    private String title;
    private String description;
    @Email(message = "Неверный формат адреса электронной почты.")
    private String executor;
    private StatusType status;
    private PriorityType priority;
}
