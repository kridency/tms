package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.TaskStatus;
import com.example.taskmanagementsystem.mappers.CommentMapper;
import com.example.taskmanagementsystem.mappers.TaskMapper;
import com.example.taskmanagementsystem.mappers.UserMapper;
import com.example.taskmanagementsystem.repositories.CommentRepository;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.specifications.TaskSpecification;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    private final TaskMapper taskMapper = TaskMapper.INSTANCE;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public Slice<TaskDto> getAllFiltered(String author, String worker, Pageable pageable) {
        Collection<TaskDto> result = taskRepository.findAll(new TaskSpecification(new HashMap<>() {{
                put("author", Optional.ofNullable(author).map(author -> userRepository.findByEmail(author)
                        .orElseThrow(() -> new NotFoundException("Автор с Email = " + author + " не найден.")))
                        .orElse(null));
                put("worker", Optional.ofNullable(worker).map(worker -> userRepository.findByEmail(worker)
                        .orElseThrow(() -> new NotFoundException("Исполнитель с Email = " + worker + " не найден.")))
                        .orElse(null));
            }}), pageable).getContent().stream().map(taskMapper::taskToTaskDto).toList();
        return new SliceImpl<>(result.stream().toList(), pageable, result.iterator().hasNext());
    }

    public TaskDto getById(Long id) {
        return taskRepository.findById(id).map(taskMapper::taskToTaskDto)
                .orElseThrow(() -> new NotFoundException("Задача с ID = " + id + " не найдена."));
    }

    public Collection<CommentDto> getCommentsByTask(Long id) {
        return commentRepository.findAllByTaskId(id).stream().map(commentMapper::commentToCommentDto).toList();
    }

    public Long create(TaskDto taskDto) {
        if (taskDto == null) throw new BadRequestException("Неверно задан запрос.");
        return userRepository.findByEmail(taskDto.getAuthor()).map(author -> {
                    Task task = taskMapper.taskDtoToTask(taskDto, this, userService);
                    Optional.ofNullable(taskDto.getWorker()).ifPresent(worker ->
                        userRepository.findByEmail(worker).ifPresentOrElse(task::setWorker, () ->
                                {
                                    throw new NotFoundException("Исполнитель с Email = " + worker + " не найден.");
                                })
                    );
                    task.setAuthor(author);
                    return taskRepository.save(task).getId();
                }).orElseThrow(() -> new NotFoundException("Автор с Email = " + taskDto.getAuthor() + " не найден."));
    }

    public void update(TaskDto taskDto) {
        if(taskDto == null) throw new BadRequestException("Неверно задан запрос.");
        taskRepository.findById(taskDto.getId()).filter(task -> task.getAuthor().getEmail().equals(taskDto.getAuthor()))
                .ifPresentOrElse(task -> {
                    task.setTitle(taskDto.getTitle());
                    taskRepository.save(task);
                }, () -> { throw new NotFoundException("Задача с ID = " + taskDto.getId() + " и с Автором = "
                        + taskDto.getAuthor() + " не найдена."); });
    }

    public void assign(TaskDto taskDto) {
        if(taskDto == null) throw new BadRequestException("Неверно задан запрос.");
        taskRepository.findById(taskDto.getId()).filter(task -> task.getAuthor().getEmail().equals(taskDto.getAuthor()))
                .ifPresentOrElse(task -> userRepository.findByEmail(taskDto.getWorker()).ifPresentOrElse(worker -> {
                    task.setWorker(worker);
                    taskRepository.save(task);
                }
                , () -> { throw new NotFoundException("Исполнитель с Email = " + taskDto.getWorker() + " не найден"); })
                        , () -> { throw new NotFoundException("Задача с ID = " + taskDto.getId() + " и с Автором = "
                        + taskDto.getAuthor() + " не найдена."); });
    }

    public void close(Long id) {
        taskRepository.findById(id).ifPresentOrElse(task -> {
            if(!task.getStatus().equals(TaskStatus.CLOSED)) {
                task.setStatus(TaskStatus.CLOSED);
                taskRepository.save(task);
            }}, () -> { throw new NotFoundException("Задача с ID = " + id + " не найдена."); }
        );
    }

    public void delete(Long id) {
        taskRepository.findById(id).ifPresentOrElse(taskRepository::delete,
                () -> { throw new NotFoundException("Задача с ID = " + id + " не найдена."); });
    }
}
