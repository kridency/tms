package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.TaskStatus;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {
        CommentMapper.class
})
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Named("fromStatus")
    default String map(TaskStatus status) {
        return status.name();
    }

    @IterableMapping(qualifiedByName = {"CommentMapper", "commentToCommentDto"})
    @Named("fromComments")
    Collection<CommentDto> map(List<Comment> comments, @Context CommentMapper commentMapper);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "author.email", target = "author"),
            @Mapping(source = "worker.email", target = "worker"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status", qualifiedByName = "fromStatus"),
            @Mapping(source = "comments", target = "comments", qualifiedByName = "fromComments")
    })
    TaskDto taskToTaskDto(Task task, @Context CommentMapper commentMapper);

    @Named("toStatus")
    default TaskStatus map(String status) {
        return TaskStatus.valueOf(status);
    }

    @Mappings({
            @Mapping(target = "author",
                    expression = "java(userService.findByEmail(taskDto.getAuthor()))"),
            @Mapping(target = "worker",
                    expression = "java(taskDto.getWorker()!=null ? userService.findByEmail(taskDto.getWorker()):null)"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status", qualifiedByName = "toStatus"),
            @Mapping(target = "comments",
                    expression = "java(taskService.getCommentsByTask(taskDto.getId()).stream()"+
                            ".map(comment -> commentMapper.commentDtoToComment(comment, userService, " +
                            "userMapper, taskService, INSTANCE)).toList())")
    })
    Task taskDtoToTask(TaskDto taskDto, @Context UserService userService, @Context UserMapper userMapper,
                       @Context TaskService taskService, @Context CommentMapper commentMapper);
}
