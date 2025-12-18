package com.zarvekule.community.dto;

import com.zarvekule.community.enums.TargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeRequest {

    @NotNull(message = "Beğenilen nesnenin tipi (TargetType) boş olamaz.")
    private TargetType targetType;

    @NotNull(message = "Beğenilen nesnenin ID'si (TargetId) boş olamaz.")
    private Long targetId;
}