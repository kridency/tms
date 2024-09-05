package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.entities.RefreshToken;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.repositories.RefreshTokenRepository;
import com.example.taskmanagementsystem.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        var refreshToken = RefreshToken.builder()
                .userId(userId)
                .issueDate(Instant.now())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration.toMillis()))
                .token(UUID.randomUUID().toString())
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public RefreshToken checkRefreshToken(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ExpiredJwtException(Jwts.header()
                    .add("Authorization", "Bearer " + token.getToken())
                    .build(), Jwts.claims()
                    .id(String.valueOf(token.getId()))
                    .subject(userRepository.findById(token.getUserId()).map(User::getEmail)
                            .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + token.getUserId()
                                    + " не найден.")))
                    .expiration(Date.from(token.getExpiryDate()))
                    .issuedAt(Date.from(token.getIssueDate())).build(), "Refresh token was expired. Repeat signin action!");
        }

        return token;
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
