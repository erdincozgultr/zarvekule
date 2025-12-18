package com.zarvekule.venue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimRequest {
    @NotBlank(message = "Sahiplik iddiası için bir açıklama yapmalısınız.")
    private String reason;
}
