package com.zarvekule.search.service;

import com.zarvekule.search.dto.GlobalSearchResponse;
import java.util.List;

public interface GlobalSearchService {
    List<GlobalSearchResponse> search(String query);
}