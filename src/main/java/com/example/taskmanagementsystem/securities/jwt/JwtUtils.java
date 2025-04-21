package com.example.taskmanagementsystem.securities.jwt;

import com.example.taskmanagementsystem.configurations.properties.AppProperties;
import io.jsonwebtoken.ExpiredJwtException;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final AppProperties.JwtProperties properties;

    public String generateTokenFromUsername(String username) {
        Date date = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(date)
                .expiration(new Date(date.getTime() + properties.tokenExpiration().toMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret())))
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret())))
                .build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validate(String accessToken) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret())))
                    .build().parseSignedClaims(accessToken);
            return true;
        } catch(SignatureException e) {
            log.error("Invalid signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("Expired token: \n{}", e.getMessage());
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
