package com.zarvekule.campaign.dto;

import com.zarvekule.campaign.enums.GameFrequency;
import com.zarvekule.campaign.enums.GameSystem;
import com.zarvekule.campaign.enums.PlayPlatform;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CampaignRequest {
    @NotBlank(message = "Başlık gereklidir.")
    private String title;

    @NotBlank(message = "Açıklama gereklidir.")
    private String description;

    @NotNull(message = "Oyun sistemi seçilmelidir.")
    private GameSystem system;

    @NotNull(message = "Platform seçilmelidir.")
    private PlayPlatform platform;

    @NotNull(message = "Oyun sıklığı seçilmelidir.")
    private GameFrequency frequency;

    private String city;
    private String district;
    private String virtualTableLink;

    @Min(value = 1, message = "En az 1 kişilik kontenjan olmalıdır.")
    private int maxPlayers;

    private String levelRange;
}