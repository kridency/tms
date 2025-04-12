package com.example.taskmanagementsystem.securities;

import com.example.taskmanagementsystem.dto.RefreshTokenDto;
import com.example.taskmanagementsystem.entities.RefreshToken;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.mappers.UserMapper;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.securities.jwt.JwtUtils;
import com.example.taskmanagementsystem.services.TokenService;
import com.example.taskmanagementsystem.web.models.*;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public RefreshTokenDto issueToken(AuthRequest request) {
        UserDetails userDetails = loadUserByUsername(request.getEmail());
        String accessToken = jwtUtils.generateJwtToken(userDetails);
        RefreshToken refreshToken = tokenService.createRefreshToken(accessToken);
        return RefreshTokenDto.builder().accessToken(accessToken).refreshToken(refreshToken.getId()).build();
    }

    public void register(AuthRequest request) {
        String email = request.getEmail();
        User visitor = userRepository.getByEmail(email).orElse(null);
        Optional.ofNullable(visitor).ifPresentOrElse(user -> {
                    throw new EntityExistsException("Email = " + email + " уже зарегистрирован!");
                }, () -> userRepository.save(userMapper.authRequestToUser(request)));
    }

    public RefreshTokenDto refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = tokenService.findById(request.getRefreshToken());
        return Optional.ofNullable(refreshToken)
                .map(tokenService::checkRefreshToken)
                .map(RefreshToken::getAccessToken)
                .map(accessToken -> {
                    var newAccessToken = jwtUtils.generateTokenFromUsername(jwtUtils.getUsername(accessToken));
                    var newRefreshToken = tokenService.createRefreshToken(newAccessToken);
                    return new RefreshTokenDto(newAccessToken, newRefreshToken.getId());
                }).orElseThrow(() -> new BadRequestException("Refresh token not valid!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Email is: " + username));
        return new AppUserDetails(user);
    }

    public User findByEmail(String email) {
        return userRepository.getByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с Email = " + email + " не найден."));
    }
}
