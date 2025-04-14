package com.example.taskmanagementsystem.dto;

import com.example.taskmanagementsystem.entities.PriorityType;
import com.example.taskmanagementsystem.entities.StatusType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ по запросу на создание/обновление задачи.")
public class TaskDto {
    @Schema(description = "Заголовок задачи.")
    private String title;
    @Schema(description = "Описание задачи.")
    private String description;
    @Schema(description = "Адрес электронной почты автора задачи.")
    private String author;
    @Schema(description = "Адрес электронной почты исполнителя задачи.")
    private String executor;
    @Schema(description = "Текущий статус задачи.")
    private StatusType status;
    @Schema(description = "Текущий приоритет задачи.")
    private PriorityType priority;
    @Schema(description = "Перечень комментариев опубликованных к задаче.")
    private Collection<CommentDto> comments;
}
