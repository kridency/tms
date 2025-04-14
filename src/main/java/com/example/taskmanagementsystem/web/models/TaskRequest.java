package com.example.taskmanagementsystem.web.models;

import com.example.taskmanagementsystem.entities.PriorityType;
import com.example.taskmanagementsystem.entities.StatusType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на создание/обновление описания задачи.")
public class TaskRequest {
    @NotNull(message = "Не указано значение поля title.")
    @Schema(description = "Заголовок задачи.")
    private String title;
    @Schema(description = "Описание задачи.")
    private String description;
    @Email(message = "Неверный формат адреса электронной почты.")
    @Schema(description = "Электронная почта исполнителя задачи.")
    private String executor;
    @Schema(description = "Текущий статус задачи.")
    private StatusType status;
    @Schema(description = "Текущий приоритет задачи.")
    private PriorityType priority;
}
