package com.zarvekule.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WallCommentRequest {

    @NotBlank(message = "Yorum içeriği boş bırakılamaz.")
    @Size(max = 500, message = "Yorum en fazla 500 karakter olabilir.")
    private String content;

    @NotBlank(message = "Hedef kullanıcı adı belirtilmelidir.")
    private String profileOwnerUsername;
}