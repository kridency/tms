package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.web.models.CommentRequest;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
@Named("CommentMapper")
public interface CommentMapper {
    @Mappings({
            @Mapping(source = "talker.email", target = "talker"),
            @Mapping(source = "task.title", target = "taskTitle"),
            @Mapping(source = "createDate", target = "createDate"),
            @Mapping(source = "updateDate", target = "updateDate"),
            @Mapping(source = "text", target = "text")
    })
    @Named("commentToCommentDto")
    CommentDto commentToCommentDto(Comment data);

    @Mappings({
            @Mapping(source = "text", target = "text")
    })
    @Named("commentCreateRequestToComment")
    Comment commentRequestToComment(CommentRequest data);
}
