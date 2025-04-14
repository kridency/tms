package com.example.taskmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@Schema(description = "Ответ по запросу на создание/обновление комментария.")
public class CommentDto {
    @JsonProperty("create_date")
    @Schema(description = "Дата создания комментария.")
    private Instant createDate;
    @JsonProperty("update_date")
    @Schema(description = "Дата последнего обновления комментария.")
    private Instant updateDate;
    @Schema(description = "Заголовок задачи, к которому опубликован комментарий.")
    private String title;
    @Schema(description = "Текст комментария.")
    private String text;
    @Schema(description = "Адрес электронной почты комментатора.")
    private String talker;
}
