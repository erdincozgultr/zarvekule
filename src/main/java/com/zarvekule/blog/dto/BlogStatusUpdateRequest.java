package com.zarvekule.blog.dto;

import com.zarvekule.blog.enums.BlogStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlogStatusUpdateRequest {

    @NotNull(message = "Status boş olamaz")
    private BlogStatus status;

}