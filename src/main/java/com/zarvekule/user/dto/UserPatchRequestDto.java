package com.zarvekule.user.dto;

import jakarta.validation.constraints.Size;

public record UserPatchRequestDto(

        @Size(min = 2, max = 50, message = "Görünen isim 2-50 karakter arasında olmalıdır.")
        String displayName,

        @Size(max = 500, message = "Biyografi en fazla 500 karakter olabilir.")
        String bio,

        String avatarUrl,

        String bannerUrl,

        @Size(min = 6, message = "Yeni şifre en az 6 karakter olmalıdır.")
        String newPassword
) {}