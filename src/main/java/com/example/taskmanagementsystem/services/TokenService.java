package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.entities.RefreshToken;
import com.example.taskmanagementsystem.repositories.TokenRepository;
import com.example.taskmanagementsystem.securities.jwt.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;

    public RefreshToken findById(UUID id) {
        return tokenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(" Refresh token " + id + " not found "));
    }

    public RefreshToken createRefreshToken(String accessToken) {
        Instant now = Instant.now();
        return tokenRepository.save(
                new RefreshToken(accessToken, now, now.plusMillis(refreshTokenExpiration.toMillis()))
        );
    }

    public RefreshToken checkRefreshToken(RefreshToken token) {
        String username = jwtUtils.getUsername(token.getAccessToken());
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            tokenRepository.delete(token);
            throw new ExpiredJwtException(Jwts.header()
                    .add("Authorization", "Bearer " + token.getAccessToken())
                    .build(), Jwts.claims()
                    .id(String.valueOf(token.getId()))
                    .subject(username)
                    .expiration(Date.from(token.getExpiryDate()))
                    .issuedAt(Date.from(token.getIssueDate())).build(), "Refresh token was expired. Repeat signin action!");
        }

        return token;
    }

    public void delete(String accessToken) {
        tokenRepository.deleteByAccessToken(accessToken);
    }
}
