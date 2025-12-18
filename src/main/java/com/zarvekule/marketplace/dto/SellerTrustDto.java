package com.zarvekule.marketplace.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;

@Data
public class SellerTrustDto {
    private UserSummaryDto seller;
    private long accountAgeMonths;
    private long successfulSales;
    private long totalContentEntries;
}