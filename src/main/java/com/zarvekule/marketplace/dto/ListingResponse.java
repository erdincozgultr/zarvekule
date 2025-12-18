package com.zarvekule.marketplace.dto;

import com.zarvekule.marketplace.enums.ListingStatus;
import com.zarvekule.marketplace.enums.ProductCategory;
import com.zarvekule.marketplace.enums.ProductCondition;
import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ListingResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private ProductCondition condition;
    private String contactInfo;
    private String imageUrl;
    private ListingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private UserSummaryDto seller;
}