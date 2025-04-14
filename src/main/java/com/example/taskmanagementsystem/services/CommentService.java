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

    /**
     * Запускает обращение к базе данных комментариев для создания новой записи.
     * Основной метод для создания записи комментария в базе данных.
     * @param title заголовок задачи, комментарий к которому создается в базе данных
     * @param request   объект описания аттрибутов создаваемого комментария
     * @param username адрес электронной почты пользователя, отправившего запрос на создание записи комментария
     *
     * @return  объект описания результата обращения к базе данных комментариев
     */
    public CommentDto create(String title, CommentRequest request, String username) {
        Task task = taskService.findByTitleAndUsername(title, username);
        Comment comment = commentMapper.commentRequestToComment(request);
        comment.setTask(task);
        User author = task.getAuthor();
        comment.setTalker(author.getEmail().equals(username) ? author : task.getExecutor());
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    public Collection<CommentDto> list(String title, String username) {
        return findByTitleAndUsername(title, username).stream().map(commentMapper::commentToCommentDto).toList();
    }

    /**
     * Запускает обращение к базе данных комментариев для обновления существующей записи.
     * Основной метод для обновления записи комментария в базе данных.
     * @param createDate момент времени публикации комментария
     * @param request   объект описания аттрибутов обновляемого комментария
     * @param username адрес электронной почты пользователя, отправившего запрос на обновление записи комментария
     *
     * @return  объект описания результата обращения к базе данных комментариев
     */
    public CommentDto update(Instant createDate, CommentRequest request, String username) {
        return commentRepository.getByCreateDateAndTalker(createDate, userService.findByEmail(username)).map(comment ->{
            comment.setText(request.getText());
            return commentMapper.commentToCommentDto(commentRepository.save(comment));
        }).orElseThrow( () -> new NotFoundException("Комментарий от " + createDate + " не найден."));
    }

    /**
     * Запускает обращение к базе данных комментариев для удаления существующей записи.
     * Основной метод для удаления записи комментария в базе данных.
     * @param createDate   момент времени публикации комментария
     * @param username адрес электронной почты пользователя, отправившего запрос на удаление записи комментария
     *
     */
    public void delete(Instant createDate, String username) {
        commentRepository.delete(commentRepository.getByCreateDateAndTalker(createDate, userService.findByEmail(username))
                .orElseThrow(() -> new NotFoundException("Комментарий от " + createDate + " не найден.")));
    }

    /**
     * Запускает обращение к базе данных комментариев для получения перечня комментариев пользователя по указанному заголовку задачи.
     * Вспомогательный метод для получения перечня объектов комментариев, отображающих записи в базе данных.
     * @param title   заголовок задачи
     * @param username адрес электронной почты пользователя, зарегистрировавшего комментарии к задаче.
     *
     * @return  перечень объектов задачи, отображающих записи в базе данных
     */
    public Collection<Comment> findByTitleAndUsername(String title, String username) {
        return commentRepository.getAllByTaskAndTalker(taskService.findByTitleAndUsername(title, username),
                userService.findByEmail(username));
    }
}
