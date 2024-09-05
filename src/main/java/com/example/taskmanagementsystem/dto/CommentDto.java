package com.example.taskmanagementsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String talker;
    private Long task;
    private Instant date;
    private String text;
}
