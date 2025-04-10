package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.services.CommentService;
import com.example.taskmanagementsystem.web.models.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Authentication")
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Получить комментарий по идентификотору",
            description = "Получает описание комментария по идентификатору (id).")
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable(name = "id") @Parameter(description =
            "Идентификационный номер комментария") Long id) {
        CommentDto comment = commentService.getById(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @Operation(summary = "Изменить комментарий по идентификотору",
            description = "Редактирует описание комментария по идентификатору (id).")
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable @Parameter(description =
            "Идентификационный номер комментария") Long id,
                                           @RequestBody CommentDto commentDto) {
        commentDto.setId(id);
        commentService.update(commentDto);
        return new ResponseEntity<>(commentService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Удалить комментарий по идентификотору",
            description = "Удалить комментарий по идентификатору (id).")
    @DeleteMapping("/{id}")
    public ResponseEntity<SimpleResponse> deleteComment(@PathVariable @Parameter(description =
            "Идентификационный номер комментария")  Long id) {
        commentService.delete(id);
        return new ResponseEntity<>(new SimpleResponse("Комментарий с ID = " + id + " удален."), HttpStatus.NO_CONTENT);
    }
}
