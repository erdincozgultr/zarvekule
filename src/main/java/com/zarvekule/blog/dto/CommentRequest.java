package com.zarvekule.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Yorum boş olamaz.")
    private String content;

    private long blogId;

    private Long parentId;
}