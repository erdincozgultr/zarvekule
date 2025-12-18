package com.zarvekule.search.dto;

import com.zarvekule.search.enums.SearchResultType;
import lombok.Data;

@Data
public class GlobalSearchResponse {
    private String title;
    private String description;
    private String slug;
    private SearchResultType type;
    private String url;
}