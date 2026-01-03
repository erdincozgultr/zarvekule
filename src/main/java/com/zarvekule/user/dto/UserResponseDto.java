package com.zarvekule.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;

    private String displayName;
    private String bio;
    private String avatarUrl;
    private String bannerUrl;
    private String title;

    private LocalDateTime createdAt;
    private List<String> roles;

    private Long currentXp;
    private String currentRank;
    private String rankTitle;
}