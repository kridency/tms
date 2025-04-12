package com.example.taskmanagementsystem.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


import java.time.Instant;
import java.util.UUID;

@RedisHash("refresh_tokens")
@NoArgsConstructor
public class RefreshToken {
    @Id
    @Indexed
    private UUID id;
    @Indexed
    @JsonProperty("access_token")
    private String accessToken;
    @Indexed
    @JsonProperty("issue_date")
    private Instant issueDate;
    @Indexed
    @JsonProperty("expire_date")
    private Instant expiryDate;

    public RefreshToken(String accessToken, Instant issueDate, Instant expiryDate) {
        setAccessToken(accessToken);
        setIssueDate(issueDate);
        setExpiryDate(expiryDate);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Instant getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Instant issueDate) {
        this.issueDate = issueDate;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}
