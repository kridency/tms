package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
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
            @Mapping(target = "talker",
                    expression = "java(userService.findByEmail(commentDto.getTalker()))"),
            @Mapping(target = "task",
                    expression = "java(taskMapper.taskDtoToTask(taskService.getById(commentDto.getTask()), " +
                            "userService, userMapper, taskService, INSTANCE))"),
            @Mapping(source = "text", target = "text")
    })
    @Named("commentDtoToComment")
    Comment commentDtoToComment(CommentDto commentDto, @Context UserService userService, @Context UserMapper userMapper,
                                @Context TaskService taskService, @Context TaskMapper taskMapper);
}
