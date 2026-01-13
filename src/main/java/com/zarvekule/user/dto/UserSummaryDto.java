package com.zarvekule.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDto {
    private String username;
    private String displayName;
    private String avatarUrl;
    private String title;
    private List<String> roles;
}