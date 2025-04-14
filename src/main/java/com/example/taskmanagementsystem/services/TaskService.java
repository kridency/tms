package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.StatusType;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.mappers.TaskMapper;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.securities.UserService;
import com.example.taskmanagementsystem.specifications.TaskSpecification;
import com.example.taskmanagementsystem.web.models.TaskRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public Slice<TaskDto> filter(String author, String executor, Pageable pageable) {
        Collection<TaskDto> result = taskRepository.findAll(new TaskSpecification(new HashMap<>() {{
                put("author", Optional.ofNullable(author).map(userService::findByEmail).orElse(null));
                put("executor", Optional.ofNullable(executor).map(userService::findByEmail).orElse(null));
            }}), pageable).getContent().stream().map(taskMapper::taskToTaskDto).toList();
        return new SliceImpl<>(result.stream().toList(), pageable, result.iterator().hasNext());
    }

    public TaskDto create(TaskRequest request, String username) {
        User user = userService.findByEmail(username);
        try {
            return taskMapper.taskToTaskDto(findByTitleAndAuthorOrExecutor(request.getTitle(), username));
        } catch (NotFoundException e) {
            Task task = taskMapper.taskRequestToTask(request);
            task.setAuthor(user);
            Optional.ofNullable(request.getExecutor()).ifPresentOrElse(executor -> {
                        task.setExecutor(userService.findByEmail(executor));
                        Optional.ofNullable(request.getStatus()).ifPresentOrElse(task::setStatus, () ->
                                task.setStatus(StatusType.PENDING));
                    }, () -> Optional.ofNullable(request.getStatus()).ifPresent(status -> {
                        throw new BadRequestException("Установка задаче статуса " + status
                                + " без назначения исполнителя.");
                    })
            );
            Optional.ofNullable(request.getPriority()).ifPresent(task::setPriority);
            task.setComments(Collections.emptyList());
            return taskMapper.taskToTaskDto(taskRepository.save(task));
        }
    }

    public TaskDto update(TaskRequest request, String username) {
        Task task = findByTitleAndAuthorOrExecutor(request.getTitle(), username);
        if (task.getAuthor().getEmail().equals(username)) {
            Optional.ofNullable(request.getDescription()).ifPresent(task::setDescription);
            Optional.ofNullable(request.getExecutor()).ifPresent(executor -> {
                Optional.ofNullable(task.getExecutor()).ifPresentOrElse(value ->
                        task.setStatus(
                                value.getEmail().equals(executor) ? task.getStatus() : StatusType.PENDING
                        ), () -> task.setStatus(StatusType.PENDING)
                );
                task.setExecutor(userService.findByEmail(executor));
            });
            Optional.ofNullable(request.getStatus()).ifPresent(task::setStatus);
            Optional.ofNullable(request.getPriority()).ifPresent(task::setPriority);
        } else {
            Optional.ofNullable(task.getExecutor()).ifPresent(executor -> {
                if (executor.getEmail().equals(username)) {
                    Optional.ofNullable(request.getStatus()).ifPresent(task::setStatus);
                }
            });
        }
        return taskMapper.taskToTaskDto(taskRepository.save(task));
    }

    public void delete(String title, String username) {
        taskRepository.delete(findByTitleAndAuthorOrExecutor(title, username));
    }

    public Task findByTitleAndAuthorOrExecutor(String title, String username) {
        User user = userService.findByEmail(username);
        return taskRepository.getByTitleAndAuthorOrExecutor(title, user, user)
                .orElseThrow(() -> new NotFoundException("Задача " + title + " связанная с пользователем "
                        + user.getEmail() + " не найдена."));
    }
}
