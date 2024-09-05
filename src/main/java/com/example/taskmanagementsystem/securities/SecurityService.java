package com.example.taskmanagementsystem.securities;

import com.example.taskmanagementsystem.entities.RefreshToken;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.securities.jwt.JwtUtils;
import com.example.taskmanagementsystem.services.RefreshTokenService;
import com.example.taskmanagementsystem.web.models.*;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        return Optional.ofNullable(loginRequest.getEmail()).map(email -> {
            if (email.matches("\\S+@(\\S+\\.){1,}\\w+")) {
                Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

                List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

                return AuthResponse.builder().id(userDetails.getId()).token(jwtUtils.generateJwtToken(userDetails))
                        .refreshToken(refreshToken.getToken()).email(userDetails.getUsername()).roles(roles).build();
            } else throw new BadRequestException("Недопустимое значение для адреса электронной почты!");
        }).orElseThrow(() -> new BadRequestException("Не указан адрес электронной почты!"));
    }

    public void register(CreateUserRequest createUserRequest) {
        Optional.ofNullable(createUserRequest.getEmail()).ifPresentOrElse(email -> {
            if(email.matches("\\S+@(\\S+\\.){1,}\\w+")) {
                    userRepository.findByEmail(email).ifPresentOrElse(user -> {
                                throw new EntityExistsException("Email = " + email + " уже зарегистрирован!");
                            },
                            () -> {
                                var user = User.builder().email(email)
                                        .password(passwordEncoder.encode(createUserRequest.getPassword())).build();
                                user.setRoles(createUserRequest.getRoles());
                                userRepository.save(user);
                            });
            } else throw new BadRequestException(email + " - недопустимое значение для адреса электронной почты!");
        }, () -> { throw new BadRequestException("Не указан адрес электронной почты!"); });
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findRefreshToken(requestRefreshToken).map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUserId).map(userId -> {
                    User tokenOwner = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("Exception trying to get token for userId: "
                                    + userId));
                    String token = jwtUtils.generateTokenFromEmail(tokenOwner.getEmail());

                    return new RefreshTokenResponse(token, refreshTokenService.createRefreshToken(userId).getToken());
                }).orElseThrow(() -> new NotFoundException(requestRefreshToken + " Refresh token not found "));
    }

    public void logout() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(currentPrincipal instanceof AppUserDetails userDetails) {
            Long userId = userDetails.getId();

            refreshTokenService.deleteByUserId(userId);
        }
    }
}
