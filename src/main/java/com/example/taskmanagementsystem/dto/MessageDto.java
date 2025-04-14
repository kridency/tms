package com.example.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сообщение о завершенной операции.")
public class MessageDto {
    @Schema(description = "Текст сообщения.")
    private String message;
}
