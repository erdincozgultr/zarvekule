package com.zarvekule.campaign.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationRequest {
    @NotBlank(message = "DM için bir mesaj yazmalısınız.")
    private String message;
}