package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.*;
import com.example.taskmanagementsystem.web.models.TaskRequest;
import org.mapstruct.*;

import java.util.Collection;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = { CommentMapper.class}
)
@Named("TaskMapper")
public interface TaskMapper {
    @IterableMapping(qualifiedByName = {"CommentMapper", "commentToCommentDto"})
    @Named("fromComments")
    Collection<CommentDto> map(Collection<Comment> comments);

    @Mappings({
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "author.email", target = "author"),
            @Mapping(source = "executor.email", target = "executor"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "priority", target = "priority"),
            @Mapping(source = "comments", target = "comments", qualifiedByName = "fromComments")
    })
    TaskDto taskToTaskDto(Task data);

    @Named("toPriority")
    default PriorityType map(PriorityType priority) {
        return Optional.ofNullable(priority).orElse(PriorityType.LOW);
    }

    @Mappings({
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "description", target = "description"),
            @Mapping(target = "executor", ignore = true),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "priority", target = "priority", qualifiedByName = "toPriority")
    })
    Task taskRequestToTask(TaskRequest data);
}
