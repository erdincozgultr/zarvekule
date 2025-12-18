package com.zarvekule.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDto {
    private String username;
    private String displayName;
    private String avatarUrl;
    private String title;
}