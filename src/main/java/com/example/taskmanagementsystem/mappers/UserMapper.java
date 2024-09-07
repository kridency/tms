package com.example.taskmanagementsystem.mappers;

import com.example.taskmanagementsystem.dto.UserDto;
import com.example.taskmanagementsystem.entities.RoleType;
import com.example.taskmanagementsystem.entities.User;
import com.example.taskmanagementsystem.services.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { UserService.class })
@Named("UserMapper")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @IterableMapping(qualifiedByName = "commentToCommentDto")
    @Named("fromRoles")
    default Collection<String> map(Set<RoleType> roles) {
        return roles.stream().map(RoleType::name).collect(Collectors.toSet());
    }

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "roles", target = "roles", qualifiedByName = "fromRoles")
    })
    UserDto userToUserDto(User user);

    @Named("toRoles")
    default Set<RoleType> map(Collection<String> roles) {
        return roles.stream().map(RoleType::valueOf).collect(Collectors.toSet());
    }

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "roles", target = "roles", qualifiedByName = "toRoles")
    })
    User userDtoToUser(UserDto user);
}
