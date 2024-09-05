package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.entities.Task;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, Long>,
        CrudRepository<Task, Long>, JpaSpecificationExecutor<Task> {
}
