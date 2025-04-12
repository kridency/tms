package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.entities.Comment;
import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, UUID>, CrudRepository<Comment, UUID> {
    Collection<Comment> getAllByTaskAndTalker(Task task, User talker);
    Optional<Comment> getByCreateDateAndTalker(Instant createDate, User talker);
}
