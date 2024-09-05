package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.configurations.properties.AppProperties;
import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.TaskStatus;
import com.example.taskmanagementsystem.services.CommentService;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final CommentService commentService;
    private final AppProperties properties;

    @Operation(summary = "Получить перечень задач по критерию поиска",
            description = "Фомирует перечень задач по автору (author) и/или исполнителю (worker). Вывод производится " +
                    " пакетами с размером не превосходящим предел (limit).")
    @GetMapping
    public ResponseEntity<?> filterBy(@RequestParam(value = "author", required = false)
                                          @Parameter(description = "Автор задач для выборки") String author,
                                      @RequestParam(value = "worker", required = false)
                                      @Parameter(description = "Исполнитель задач для выборки") String worker,
                                      @RequestParam(value = "offset", required = false)
                                          @Parameter(description = "Порядковый номер начального элемента выборки")
                                          Integer offset,
                                      @RequestParam(value = "limit", required = false)
                                          @Parameter(description = "Размер пакета выборки") Integer limit) {
        return new ResponseEntity<>(taskService.getAllFiltered(author, worker, PageRequest
                .of(Optional.ofNullable(offset).isPresent() ? offset : 0,
                Optional.ofNullable(limit).isPresent() ? limit : properties.paginationLimit())
        ), HttpStatus.OK);
    }

    @Operation(summary = "Получить задачу по идентификотору",
            description = "Получает описание задачи по идентификатору (id).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable(name = "id") @Parameter(description = "Идентификационный " +
            "номер задачи") Long id) {
        TaskDto taskDto = taskService.getById(id);
        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @Operation(summary = "Зарегистрировать новую задачу",
            description = "Регистрирует задачу с указанным заголовком.")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    public ResponseEntity<TaskDto> postTask(@RequestBody TaskCreateRequest taskCreateRequest,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        var taskDto = new TaskDto();
        taskDto.setTitle(taskCreateRequest.getTitle());
        taskDto.setAuthor(userDetails.getUsername());
        taskDto.setStatus(TaskStatus.OPEN.name());
        return new ResponseEntity<>(taskService.getById(taskService.create(taskDto)), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить задачу",
            description = "Обновляет заголовок для задачи с указанным идентификатором.")
    @PutMapping("/{id}")
    @PreAuthorize("(@taskService.getById(#id).author == principal.username)")
    public ResponseEntity<TaskDto> updateTask(@PathVariable(name = "id") @Parameter(description = "Идентификационный " +
            "номер задачи") Long id,
                                              @RequestBody TaskUpdateRequest taskUpdateRequest,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        var taskDto = taskService.getById(id);
        taskDto.setTitle(taskUpdateRequest.getTitle());
        taskDto.setAuthor(userDetails.getUsername());
        taskService.update(taskDto);
        return new ResponseEntity<>(taskService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Назначить задачу",
            description = "Назначает исполнителя для задачи с указанным идентификатором.")
    @PutMapping("/{id}/assign")
    @PreAuthorize("@taskService.getById(#id).author == principal.username")
    public ResponseEntity<TaskDto> assignTask(@PathVariable(name = "id") @Parameter(description = "Идентификационный " +
            "номер задачи") Long id,
                                              @RequestBody TaskAssignRequest taskAssignRequest,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        var taskDto = taskService.getById(id);
        taskDto.setWorker(taskAssignRequest.getWorker());
        taskDto.setAuthor(userDetails.getUsername());
        taskService.assign(taskDto);
        return new ResponseEntity<>(taskService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Закрыть задачу",
            description = "Изменяет статус на CLOSED у задачи с указанным идентификатором.")
    @PutMapping("/{id}/close")
    @PreAuthorize("(@taskService.getById(#id).worker == principal.username) or " +
            "(@taskService.getById(#id).author == principal.username)")
    public ResponseEntity<SimpleResponse> closeTask(@PathVariable(name = "id") @Parameter(description =
            "Идентификационный номер задачи") Long id) {
        taskService.close(id);
        return new ResponseEntity<>(new SimpleResponse("Задача с ID = " + id + " закрыта."), HttpStatus.OK);
    }

    @Operation(summary = "Удалить задачу",
            description = "Удаляет задачу с указанным идентификатором.")
    @DeleteMapping("/{id}")
    @PreAuthorize("(@taskService.getById(#id).author == principal.username)")
    public ResponseEntity<SimpleResponse> deleteTask(@PathVariable(name = "id") @Parameter(description =
            "Идентификационный номер задачи") Long id) {
        taskService.delete(id);
        return new ResponseEntity<>(new SimpleResponse("Задача с ID = " + id + " удалена."), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Добавить комментарий", description = "Добавляет комментарий к задачу с указанным " +
            "идентификатором.")
    @PostMapping(path = "/{id}/comments")
    public ResponseEntity<CommentDto> postComment(@PathVariable(name="id") @Parameter(description =
            "Идентификационный номер задачи") Long id,
                                                  @RequestBody CommentCreateRequest commentCreateRequest,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        var commentDto = new CommentDto();
        commentDto.setTalker(userDetails.getUsername());
        commentDto.setTask(id);
        commentDto.setText(commentCreateRequest.getText());
        return new ResponseEntity<>(commentService.create(taskService.getById(id), commentDto),
                HttpStatus.CREATED);
    }
}
