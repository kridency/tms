package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.UserDto;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.web.models.AuthRequest;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring",
        uses = { RoleMapper.class })
@Named("UserMapper")
public interface UserMapper {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "roles", target = "roles")
    })
    UserDto userToUserDto(User user);

    @Named("encodePassword")
    default String encodePassword(String plain) {
        return encoder.encode(plain);
    }

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password", qualifiedByName = "encodePassword"),
            @Mapping(source = "roles", target = "roles", qualifiedByName = {"RoleMapper", "toRoles"})
    })
    User authRequestToUser(AuthRequest data);
}
