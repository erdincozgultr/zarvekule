package com.zarvekule.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BanRequest {
    @NotBlank(message = "Ban sebebi belirtilmelidir.")
    private String reason;
}