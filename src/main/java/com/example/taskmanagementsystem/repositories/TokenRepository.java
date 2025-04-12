package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenRepository extends CrudRepository<RefreshToken, UUID> {
    void deleteByAccessToken(String accessToken);
}
