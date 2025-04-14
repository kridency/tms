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

    /**
     * Формирование параметра фильтрации выборки из базы данных задач.
     * Вспомогательный метод формирования параметра фильтрации выборки из базы данных задач.
     * @param key   наименование критерия для поиска задач на основе электронного адреса автора
     *
     * @return  пара ключ значение параметра фильтрации выборки
     */
    private Map.Entry<String, User> setCriteriaParameter(String key) {
        return new AbstractMap.SimpleEntry<>(key, Optional.ofNullable(key).map(userService::findByEmail).orElse(null));
    }

    /**
     * Составляет выборку из базы данных задач согласно указанным параметрам и настройка пагинации.
     * Основной метод для составления выборки на основе критериев фильтрации.
     * @param author   значение критерия для поиска задач на основе электронного адреса автора
     * @param executor значение критерия для поиска задач на основе электронного адреса исполнителя
     * @param pageable настройки пагинации для составления выборки
     *
     * @return  выборка из базы данных задач согласно установленным параметрам
     */
    public Slice<TaskDto> filter(String author, String executor, Pageable pageable) {
        Collection<TaskDto> result = taskRepository.findAll(new TaskSpecification(new HashMap<>() {{
                entrySet().add(setCriteriaParameter(author));
                entrySet().add(setCriteriaParameter(executor));
            }}), pageable).getContent().stream().map(taskMapper::taskToTaskDto).toList();
        return new SliceImpl<>(result.stream().toList(), pageable, result.iterator().hasNext());
    }

    /**
     * Запускает обращение к базе данных задач для создания новой записи.
     * Основной метод для создания записи задачи в базе данных.
     * @param request   объект описания аттрибутов создаваемой задачи
     * @param username адрес электронной почты пользователя, отправившего запрос на создание записи задачи
     *
     * @return  объект описания результата обращения к базе данных задач
     */
    public TaskDto create(TaskRequest request, String username) {
        User user = userService.findByEmail(username);
        try {
            return taskMapper.taskToTaskDto(findByTitleAndUsername(request.getTitle(), username));
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

    /**
     * Запускает обращение к базе данных задач для обновления существующей записи.
     * Основной метод для обновления записи задачи в базе данных.
     * @param request   объект описания аттрибутов обновляемой задачи
     * @param username адрес электронной почты пользователя, отправившего запрос на обновление записи задачи
     *
     * @return  объект описания результата обращения к базе данных задач
     */
    public TaskDto update(TaskRequest request, String username) {
        Task task = findByTitleAndUsername(request.getTitle(), username);
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

    /**
     * Запускает обращение к базе данных задач для удаления существующей записи.
     * Основной метод для удаления записи задачи в базе данных.
     * @param title   заголовок удаляемой задачи
     * @param username адрес электронной почты пользователя, отправившего запрос на удаление записи задачи
     *
     */
    public void delete(String title, String username) {
        taskRepository.delete(findByTitleAndUsername(title, username));
    }

    /**
     * Запускает обращение к базе данных задач для получения объекта по указанному заголовку, автору или исполнителю задачи.
     * Вспомогательный метод для получения объекта задачи, отображающего запись в базе данных.
     * @param title   заголовок задачи
     * @param username адрес электронной почты пользователя, зарегистрировавшего задачу или назначенного исполнителем по задаче.
     *
     * @return  объект задачи, отображающий запись в базе данных
     */
    public Task findByTitleAndUsername(String title, String username) {
        User user = userService.findByEmail(username);
        return taskRepository.getByTitleAndAuthorOrExecutor(title, user, user)
                .orElseThrow(() -> new NotFoundException("Задача " + title + " связанная с пользователем "
                        + user.getEmail() + " не найдена."));
    }
}
