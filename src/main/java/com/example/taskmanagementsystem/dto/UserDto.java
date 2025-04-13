package com.example.taskmanagementsystem.dto;

import com.example.taskmanagementsystem.entities.RoleType;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private Set<RoleType> roles;
}
