package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.services.CommentService;
import com.example.taskmanagementsystem.web.models.CommentRequest;
import com.example.taskmanagementsystem.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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

    @Operation(summary = "Добавить комментарий",
            description = "Добавляет комментарий к задаче с указанным заголовком (title).")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto postComment(@RequestParam(name = "title")
                                      @Parameter(description = "Заголовок задачи")
                                      @NotNull(message = "Не указан заголовок задачи (title)") String title,
                                  @RequestBody @Valid CommentRequest request,
                                  @AuthenticationPrincipal String username) {
        return commentService.create(title, request, username);
    }

    @Operation(summary = "Получить перечень комментариев",
            description = "Получает описание комментариев по заголовку задачи (title).")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<CommentDto> listComments(@RequestParam(name = "title")
                                                   @Parameter(description = "Заголовок задачи")
                                                   @NotNull(message = "Не указан заголовок задачи (title)") String title,
                                               @AuthenticationPrincipal String username) {
        return commentService.list(title, username);
    }

    @Operation(summary = "Изменить комментарий по дате публикации",
            description = "Редактирует описание комментария по дате публикации (create_date).")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public CommentDto updateComment(@RequestParam(name = "create_date")
                                        @Parameter(description = "Идентификационный номер комментария")
                                        @Past(message = "Дата не принадлежит прошедшему периоду") Instant createDate,
                                    @RequestBody @Valid CommentRequest request,
                                    @AuthenticationPrincipal String username) {
        return commentService.update(createDate, request, username);
    }

    @Operation(summary = "Удалить комментарий по дате публикации",
            description = "Удалить комментарий по дате публикации (create_date).")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public MessageDto deleteComment(@RequestParam(name = "create_date")
                                        @Parameter(description = "Идентификационный номер комментария")
                                        @Past(message = "Дата не принадлежит прошедшему периоду") Instant createDate,
                                    @AuthenticationPrincipal String username) {
        commentService.delete(createDate, username);
        return new MessageDto("Комментарий от " + createDate + " удален.");
    }
}
