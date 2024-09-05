package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.mappers.CommentMapper;
import com.example.taskmanagementsystem.mappers.TaskMapper;
import com.example.taskmanagementsystem.mappers.UserMapper;
import com.example.taskmanagementsystem.repositories.CommentRepository;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final TaskRepository newsRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final TaskMapper taskMapper = TaskMapper.INSTANCE;

    public CommentDto getById(Long id) {
        return commentRepository.findById(id).map(commentMapper::commentToCommentDto)
                .orElseThrow(() -> new NotFoundException("Комментарий с ID = " + id + " не найден."));
    }

    public CommentDto create(TaskDto taskDto, CommentDto commentDto) {
        if(taskDto == null || commentDto == null) throw new BadRequestException("Неверно задан запрос.");
        return newsRepository.findById(taskDto.getId()).map(task ->
                userRepository.findByEmail(commentDto.getTalker()).map(user -> {
                    Comment comment = commentMapper.commentDtoToComment(commentDto, userService, userMapper, taskService,
                            taskMapper);
                    comment.setTask(task);
                    comment.setTalker(user);
                    return commentMapper.commentToCommentDto(commentRepository.save(comment));
                }).orElseThrow(()->new NotFoundException("Пользователь с Email ="+commentDto.getTalker()+" не найден."))
        ).orElseThrow(() -> new NotFoundException("Задача с ID = " + taskDto.getId() + " не найдена."));
    }

    public void update(CommentDto commentDto) {
        if(commentDto == null) throw new BadRequestException("Неверно задан запрос.");
        commentRepository.findById(commentDto.getId()).ifPresentOrElse(comment -> {
            comment.setText(commentDto.getText());
            commentRepository.save(comment);
        }, () -> { throw new NotFoundException("Комментарий с ID = " + commentDto.getId() + " не найден."); });
    }

    public void delete(Long id) {
        commentRepository.findById(id).ifPresentOrElse(commentRepository::delete,
                () -> { throw new NotFoundException("Комментарий с ID = " + id + " не найден."); });
    }
}
