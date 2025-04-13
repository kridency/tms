package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.services.CommentService;
import com.example.taskmanagementsystem.web.models.CommentRequest;
import com.example.taskmanagementsystem.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;

@RestController
@SecurityRequirement(name = "Authentication")
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Добавить комментарий", description = "Добавляет комментарий к задаче с указанным " +
            "заголовком.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto postComment(@RequestBody CommentRequest request,
                                                  @AuthenticationPrincipal String username) {
        return commentService.create(request, username);
    }

    @Operation(summary = "Получить комментарий по идентификатору",
            description = "Получает описание комментария по идентификатору (id).")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<CommentDto> listComments(@RequestParam(name = "taskTitle")
                                                         @Parameter(description = "Идентификационный номер комментария")
                                                     String taskTitle,
                                                             @AuthenticationPrincipal String username) {
        return commentService.listComments(taskTitle, username);
    }

    @Operation(summary = "Изменить комментарий по идентификатору",
            description = "Редактирует описание комментария по идентификатору (id).")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public CommentDto updateComment(@RequestParam(name = "create_date")
                                                        @Parameter(description = "Идентификационный номер комментария")
                                                        Instant createDate,
                                           @RequestBody CommentRequest request,
                                                    @AuthenticationPrincipal String username) {
        return commentService.update(createDate, request, username);
    }

    @Operation(summary = "Удалить комментарий по идентификатору",
            description = "Удалить комментарий по идентификатору (id).")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public MessageDto deleteComment(@RequestParam(name = "create_date")
                                                            @Parameter(description = "Идентификационный номер комментария")
                                                            Instant createDate,
                                    @AuthenticationPrincipal String username) {
        commentService.delete(createDate, username);
        return new MessageDto("Комментарий от " + createDate + " удален.");
    }
}
