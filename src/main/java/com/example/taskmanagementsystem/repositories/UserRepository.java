package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> getByEmail(String email);
    Optional<User> deleteByEmail(String email);
}
