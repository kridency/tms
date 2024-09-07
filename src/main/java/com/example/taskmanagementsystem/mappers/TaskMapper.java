package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.TaskStatus;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {
        UserMapper.class,
        UserService.class,
        CommentMapper.class
})
@Named("TaskMapper")
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Named("fromStatus")
    default String map(TaskStatus status) {
        return status.name();
    }

    @IterableMapping(qualifiedByName = {"CommentMapper", "commentToCommentDto"})
    @Named("fromComments")
    Collection<CommentDto> map(List<Comment> comments);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "author.email", target = "author"),
            @Mapping(source = "worker.email", target = "worker"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status", qualifiedByName = "fromStatus"),
            @Mapping(source = "comments", target = "comments", qualifiedByName = "fromComments")
    })
    TaskDto taskToTaskDto(Task task);

    @Named("toStatus")
    default TaskStatus map(String status) {
        return TaskStatus.valueOf(status);
    }

    @Mappings({
            @Mapping(source = "author", target = "author", qualifiedByName = "getUserEntity"),
            @Mapping(source = "worker", target = "worker", qualifiedByName = "getUserEntity"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status", qualifiedByName = "toStatus"),
            @Mapping(source = "comments", target = "comments", qualifiedByName = "toComments")
    })
    Task taskDtoToTask(TaskDto taskDto, @Context TaskService taskService, @Context UserService userService);

    @IterableMapping(qualifiedByName = {"CommentMapper", "commentDtoToComment"})
    @Named("toComments")
    List<Comment> map(Collection<CommentDto> comments, @Context TaskService taskService, @Context UserService userService);

    @Named("getUserEntity")
    default User map(String email, @Context UserService userService) {
        return userService.findByEmail(email);
    }
}
