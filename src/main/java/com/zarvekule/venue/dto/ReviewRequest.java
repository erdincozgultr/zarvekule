package com.zarvekule.venue.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @Min(1) @Max(5)
    private int rating;

    @NotBlank(message = "Yorum boş olamaz.")
    private String comment;
}
