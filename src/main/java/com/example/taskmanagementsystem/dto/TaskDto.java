package com.example.taskmanagementsystem.dto;

import com.example.taskmanagementsystem.entities.PriorityType;
import com.example.taskmanagementsystem.entities.StatusType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collection;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {
    private Instant date;
    private String title;
    private String description;
    private StatusType status;
    private PriorityType priority;
    private Collection<CommentDto> comments;
    private String author;
}
