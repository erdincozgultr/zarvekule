package com.zarvekule.moderation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Moderasyon işlemi için genel DTO
 * Her moderasyon işleminde sebep belirtilmesi zorunlu
 */
@Data
public class ModerationAction {

    @NotBlank(message = "Moderasyon sebebi belirtilmelidir")
    @Size(min = 10, max = 500, message = "Sebep 10-500 karakter arasında olmalıdır")
    private String reason;

    // Opsiyonel: Kullanıcıya gönderilecek özel mesaj
    @Size(max = 1000, message = "Mesaj maksimum 1000 karakter olabilir")
    private String messageToUser;
}