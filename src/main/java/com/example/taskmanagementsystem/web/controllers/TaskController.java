package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.configurations.properties.AppProperties;
import com.example.taskmanagementsystem.dto.MessageDto;
import com.example.taskmanagementsystem.dto.TaskDto;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
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
            description = "Формирует перечень задач по автору (author) и/или исполнителю (executor). Вывод производится "
                    + " пакетами с размером не превосходящим предел (limit) начиная с элемента под номером (offset).")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Slice<TaskDto> list(@RequestParam(value = "author", required = false)
                                   @Parameter(description = "Автор задач для выборки") String author,
                               @RequestParam(value = "executor", required = false)
                                      @Parameter(description = "Исполнитель задач для выборки") String executor,
                               @RequestParam(value = "offset", required = false)
                                   @Parameter(description = "Порядковый номер начального элемента выборки") Integer offset,
                               @RequestParam(value = "limit", required = false)
                                          @Parameter(description = "Размер пакета выборки") Integer limit) {
        return taskService.filter(author, executor, PageRequest.of(
                Optional.ofNullable(offset).orElse(0),
                Optional.ofNullable(limit).orElse(properties.paginationLimit())
        ));
    }

    @Operation(summary = "Получить описание задачи",
            description = "Получает описание задачи по заголовку (title).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TaskDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Задача не найдена", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON)
            })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(params = { "title" })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskDto getTask(@RequestParam @NotNull(message = "Не указан заголовок задачи (title)") String title,
                           @AuthenticationPrincipal String username) {
        return taskMapper.taskToTaskDto(taskService.findByTitleAndUsername(title, username));
    }

    @Operation(summary = "Зарегистрировать новую задачу",
            description = "Регистрирует задачу с аттрибутами указанными в теле запроса.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public TaskDto createTask(@RequestBody @Valid TaskRequest request, @AuthenticationPrincipal String username) {
        return taskService.create(request, username);
    }

    @Operation(summary = "Обновить задачу",
            description = "Обновляет аттрибуты задачи указанными в теле запроса значениями.")
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping
    @Transactional
    public TaskDto updateTask(@RequestBody @Valid TaskRequest request,
                              @AuthenticationPrincipal String username) {
        return taskService.update(request, username);
    }

    @Operation(summary = "Удалить задачу",
            description = "Удаляет задачу с указанным заголовком (title).")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public MessageDto deleteTask(@RequestParam("title")
                                     @NotNull(message = "Не указан заголовок задачи (title)") String title,
                                 @AuthenticationPrincipal String username) {
        taskService.delete(title, username);
        return new MessageDto("Задача = " + title + " удалена.");
    }
}
