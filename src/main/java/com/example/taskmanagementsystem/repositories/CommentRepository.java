package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.entities.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long>, CrudRepository<Comment, Long> {
    List<Comment> findAllByTaskId(Long task);
}
