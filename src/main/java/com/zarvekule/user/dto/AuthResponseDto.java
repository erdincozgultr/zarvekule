package com.zarvekule.user.dto;

public record AuthResponseDto(
        String accessToken,
        UserResponseDto user
) {}
