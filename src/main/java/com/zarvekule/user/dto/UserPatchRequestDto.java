package com.zarvekule.user.dto;

import jakarta.validation.constraints.Size;

public record UserPatchRequestDto(

        String displayName,
        String bio,
        String title,
        String avatarUrl,
        String bannerUrl,
        String newPassword
) {
}