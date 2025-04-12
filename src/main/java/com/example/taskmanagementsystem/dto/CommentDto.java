package com.example.taskmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class CommentDto {
    @JsonProperty("create_date")
    private Instant createDate;
    @JsonProperty("update_date")
    private Instant updateDate;
    private String taskTitle;
    private String text;
    private String talker;
}
