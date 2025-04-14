package com.example.taskmanagementsystem.web.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание/обновление комментария к задаче.")
public class CommentRequest {
    @Schema(description = "Текст комментария к задаче.")
    private String text;
}
