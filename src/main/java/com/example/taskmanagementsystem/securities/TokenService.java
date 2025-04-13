package com.example.taskmanagementsystem.securities;

import com.example.taskmanagementsystem.dto.TokenDto;
import com.example.taskmanagementsystem.entities.RefreshToken;
import com.example.taskmanagementsystem.entities.User;
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

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;

    public TokenDto create(String username) {
        User user = userService.findByEmail(username);
        Instant issueDate = Instant.now();
        Instant expireDate = Instant.now().plusMillis(refreshTokenExpiration.toMillis());
        String accessToken = jwtUtils.generateTokenFromUsername(username);
        RefreshToken token = new RefreshToken(accessToken, issueDate, expireDate);
        token.setId(user.getId());
        tokenRepository.save(token);
        return TokenDto.builder().accessToken(accessToken).build();
    }

    public TokenDto update(String username) {
        User user = userService.findByEmail(username);
        String accessToken = validate(findById(user.getId())).getAccessToken();
        return create(jwtUtils.getUsername(accessToken));
    }

    public void delete(String accessToken) {
        tokenRepository.deleteByAccessToken(accessToken);
    }

    public RefreshToken validate(RefreshToken token) {
        String username = jwtUtils.getUsername(token.getAccessToken());
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            tokenRepository.delete(token);
            throw new ExpiredJwtException(Jwts.header()
                    .add("Authorization", "Bearer " + token.getAccessToken())
                    .build(), Jwts.claims()
                    .id(String.valueOf(token.getId()))
                    .subject(username)
                    .expiration(Date.from(token.getExpiryDate()))
                    .issuedAt(Date.from(token.getIssueDate())).build(), "Refresh token was expired. Repeat login action!");
        }

        return token;
    }

    public RefreshToken findById(UUID refreshToken) {
        return tokenRepository.findById(refreshToken)
                .orElseThrow(() -> new NotFoundException(" Refresh token " + refreshToken + " not found "));
    }
}
