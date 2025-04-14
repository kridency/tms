package com.example.taskmanagementsystem.securities;

import com.example.taskmanagementsystem.dto.UserDto;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.mappers.UserMapper;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.web.models.*;
import jakarta.persistence.EntityExistsException;
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
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Запускает обращение к базе данных пользователей для создания новой записи.
     * Основной метод для создания записи пользователя в базе данных.
     * @param request   объект описания аттрибутов создаваемой учетной записи пользователя
     *
     * @return  объект описания результата обращения к базе данных пользователей
     */
    public UserDto create(AuthRequest request) {
        String email = request.getEmail();
        userRepository.getByEmail(email).ifPresent(user -> { throw new EntityExistsException("Email = " + email
                + " уже зарегистрирован!");});
        return userMapper.userToUserDto(userRepository.save(userMapper.authRequestToUser(request)));
    }

    /**
     * Запускает обращение к базе данных пользователей для обновления существующей записи.
     * Основной метод для обновления записи пользователя в базе данных.
     * @param request   объект описания аттрибутов создаваемой учетной записи пользователя
     * @param username  адрес электронной почты пользователя, отправившего запрос на обновление учетной записи
     *
     * @return  объект описания результата обращения к базе данных пользователей
     */
    public UserDto update(AuthRequest request, String username) {
        User user = findByEmail(username);
        String email = request.getEmail();
        userRepository.getByEmail(email).ifPresent(x -> { throw new EntityExistsException("Email = " + email
                + " уже зарегистрирован!");});
        user.setEmail(email);
        user.setPassword(userMapper.encodePassword(request.getPassword()));
        Optional.ofNullable(request.getRoles()).ifPresent(roles -> roles.forEach(role -> user.getRoles().add(role)));
        return userMapper.userToUserDto(userRepository.save(user));
    }

    /**
     * Запускает обращение к базе данных пользователей для удаления существующей записи.
     * Основной метод для удаления записи пользователя в базе данных.
     * @param username  адрес электронной почты пользователя, отправившего запрос на удаление учетной записи
     *
     * @return  объект описания результата обращения к базе данных пользователей
     */
    public UserDto delete(String username) {
        return userRepository.deleteByEmail(username).map(userMapper::userToUserDto).orElse(null);
    }

    /**
     * Преобразует объект отображения записи базы данных в объект используемый spring security.
     * Перегрузка метода для получения объекта spring security.
     * @param username  адрес электронной почты пользователя, отправившего запрос на аутентификацию
     *
     * @return  объект описания пользователя используемый spring security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Email is: " + username));
        return new AppUserDetails(user);
    }

    /**
     * Запускает обращение к базе данных пользователей для получения объекта записи согласно указанному адресу электронной почты.
     * Вспомогательный метод для получения записи пользователя из базы данных.
     * @param email  адрес электронной почты искомого пользователя
     *
     * @return  объект отображения записи базы данных пользователей
     */
    public User findByEmail(String email) {
        return userRepository.getByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с Email = " + email + " не найден."));
    }
}
