package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.mappers.CommentMapper;
import com.example.taskmanagementsystem.repositories.CommentRepository;
import com.example.taskmanagementsystem.securities.UserService;
import com.example.taskmanagementsystem.web.models.CommentRequest;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserService userService;
    private final TaskService taskService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentDto create(String title, CommentRequest request, String username) {
        Task task = taskService.findByTitleAndAuthorOrExecutor(title, username);
        Comment comment = commentMapper.commentRequestToComment(request);
        comment.setTask(task);
        User author = task.getAuthor();
        comment.setTalker(author.getEmail().equals(username) ? author : task.getExecutor());
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    public Collection<CommentDto> list(String title, String username) {
        return findByTitleAndUsername(title, username).stream().map(commentMapper::commentToCommentDto).toList();
    }

    public CommentDto update(Instant createDate, CommentRequest request, String username) {
        return commentRepository.getByCreateDateAndTalker(createDate, userService.findByEmail(username)).map(comment ->{
            comment.setText(request.getText());
            return commentMapper.commentToCommentDto(commentRepository.save(comment));
        }).orElseThrow( () -> new NotFoundException("Комментарий от " + createDate + " не найден."));
    }

    public void delete(Instant createDate, String username) {
        commentRepository.delete(commentRepository.getByCreateDateAndTalker(createDate, userService.findByEmail(username))
                .orElseThrow(() -> new NotFoundException("Комментарий от " + createDate + " не найден.")));
    }

    public Collection<Comment> findByTitleAndUsername(String title, String username) {
        return commentRepository.getAllByTaskAndTalker(taskService.findByTitleAndAuthorOrExecutor(title, username),
                userService.findByEmail(username));
    }
}
