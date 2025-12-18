package com.zarvekule.user.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileDto {
    private String username;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private String bannerUrl;
    private String title;
    private LocalDateTime joinedAt;
    private List<String> roles;
}