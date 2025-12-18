package com.zarvekule.user.mapper;

import com.zarvekule.user.dto.*;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.entity.Role;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        if (user == null) return null;

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        dto.setDisplayName(user.getDisplayName());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBannerUrl(user.getBannerUrl());
        dto.setTitle(user.getTitle());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public UserSummaryDto toSummaryDto(User user) {
        if (user == null) return null;

        return new UserSummaryDto(
                user.getUsername(),
                user.getDisplayName() != null ? user.getDisplayName() : user.getUsername(),
                user.getAvatarUrl(),
                user.getTitle() != null ? user.getTitle() : "Gezgin"
        );
    }

    public UserProfileDto toProfileDto(User user) {
        if (user == null) return null;

        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBannerUrl(user.getBannerUrl());
        dto.setTitle(user.getTitle() != null ? user.getTitle() : "Gezgin");
        dto.setJoinedAt(user.getCreatedAt());

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public User toEntity(UserRequestDto requestDto) {
        if (requestDto == null) return null;

        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());

        if (requestDto.getDisplayName() != null && !requestDto.getDisplayName().isBlank()) {
            user.setDisplayName(requestDto.getDisplayName());
        }

        return user;
    }
}