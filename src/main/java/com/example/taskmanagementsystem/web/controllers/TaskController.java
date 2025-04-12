package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.configurations.properties.AppProperties;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.mappers.TaskMapper;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@SecurityRequirement(name = "Authentication")
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final AppProperties properties;
    private final TaskMapper taskMapper;

    @Operation(summary = "Получить перечень задач по критерию поиска",
            description = "Формирует перечень задач по автору (author) и/или исполнителю (worker). Вывод производится " +
                    " пакетами с размером не превосходящим предел (limit).")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Slice<TaskDto> list(@RequestParam(value = "author", required = false)
                                          @Parameter(description = "Автор задач для выборки") String author,
                                   @RequestParam(value = "executor", required = false)
                                      @Parameter(description = "Исполнитель задач для выборки") String executor,
                                   @RequestParam(value = "offset", required = false)
                                          @Parameter(description = "Порядковый номер начального элемента выборки")
                                          Integer offset,
                                   @RequestParam(value = "limit", required = false)
                                          @Parameter(description = "Размер пакета выборки") Integer limit) {
        return taskService.filter(author, executor, PageRequest.of(
                Optional.ofNullable(offset).orElse(0),
                Optional.ofNullable(limit).orElse(properties.paginationLimit())
        ));
    }

    @Operation(summary = "Получить описание задачи",
            description = "Получает описание задачи по идентификатору (id).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content) })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(params = { "title" })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskDto getTask(@RequestParam String title, @AuthenticationPrincipal String username) {
        return taskMapper.taskToTaskDto(taskService.findByTitleAndAuthorOrExecutor(title, username));
    }

    @Operation(summary = "Зарегистрировать новую задачу",
            description = "Регистрирует задачу с указанным в теле запроса заголовком.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskDto createTask(@RequestBody TaskRequest request,
                                              @AuthenticationPrincipal String username) {
        return taskService.create(request, username);
    }

    @Operation(summary = "Обновить задачу",
            description = "Обновляет аттрибуты задачи с указанным в теле запроса заголовком.")
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping
    public TaskDto updateTask(@RequestBody TaskRequest request,
                                              @AuthenticationPrincipal String username) {
        return taskService.update(request, username);
    }

    @Operation(summary = "Удалить задачу",
            description = "Удаляет задачу с указанным в теле запроса заголовком.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public SimpleResponse deleteTask(@RequestBody TaskRequest request,
                                                     @AuthenticationPrincipal String username) {
        taskService.delete(request, username);
        return new SimpleResponse("Задача = " + request.getTitle() + " удалена.");
    }
}
