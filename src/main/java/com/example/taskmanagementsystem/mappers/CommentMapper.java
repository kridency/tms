package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {
        TaskService.class,
        UserService.class,
        TaskMapper.class,
        UserMapper.class})
@Named("CommentMapper")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "talker.email", target = "talker"),
            @Mapping(source = "task.id", target = "task"),
            @Mapping(source = "text", target = "text")
    })
    @Named("commentToCommentDto")
    CommentDto commentToCommentDto(Comment comment);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "talker", target = "talker", qualifiedByName = "getTalker"),
            @Mapping(source = "task", target = "task", qualifiedByName = "getTask"),
            @Mapping(source = "text", target = "text")
    })
    @Named("commentDtoToComment")
    Comment commentDtoToComment(CommentDto commentDto, @Context TaskService taskService, @Context UserService userService);

    @Named("getTask")
    default Task map(Long id, @Context TaskService taskService, @Context UserService userService) {
        return TaskMapper.INSTANCE.taskDtoToTask(taskService.getById(id), taskService, userService);
    }

    @Named("getTalker")
    default User map(String email, @Context UserService userService) {
        return userService.findByEmail(email);
    }
}
