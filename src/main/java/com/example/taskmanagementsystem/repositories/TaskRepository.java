package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, UUID>, CrudRepository<Task, UUID>,
        JpaSpecificationExecutor<Task> {
    Optional<Task> getByTitleAndAuthorOrExecutor(String title, User author, User executor);
}
