package com.zarvekule.search.controller;

import com.zarvekule.search.dto.GlobalSearchResponse;
import com.zarvekule.search.service.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService searchService;

    @GetMapping
    public ResponseEntity<List<GlobalSearchResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(searchService.search(q));
    }
}