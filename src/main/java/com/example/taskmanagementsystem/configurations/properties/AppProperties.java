package com.example.taskmanagementsystem.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app")
public record AppProperties(int paginationLimit) {

    @ConfigurationProperties(prefix = "app.jwt")
    public record JwtProperties(String secret, Duration tokenExpiration, Duration refreshTokenExpiration) {
    }
}
