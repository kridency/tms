package com.example.taskmanagementsystem.securities.jwt;

import com.example.taskmanagementsystem.configurations.properties.AppProperties;
import com.example.taskmanagementsystem.securities.AppUserDetails;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {
    private final AppProperties.JwtProperties properties;

    public String generateJwtToken(AppUserDetails userDetails) {
        return generateTokenFromEmail(userDetails.getUsername());
    }

    public String generateTokenFromEmail(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + properties.tokenExpiration().toMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret())))
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret())))
                .build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validate(String authToken) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret())))
                    .build().parseSignedClaims(authToken);
            return true;
        } catch(SignatureException e) {
            log.error("Invalid signature: {}", e.getMessage());
        } catch(MalformedJwtException e) {
            log.error("Invalid token: {}",e.getMessage());
        } catch(UnsupportedJwtException e) {
            log.error("Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
