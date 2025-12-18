package com.zarvekule.blog.controller;

import com.zarvekule.blog.dto.BlogEntryRequest;
import com.zarvekule.blog.dto.BlogEntryResponse;
import com.zarvekule.blog.dto.BlogEntrySummary;
import com.zarvekule.blog.service.BlogEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogEntryController {

    private final BlogEntryService blogService;

    @GetMapping("/list/public")
    public ResponseEntity<Page<BlogEntrySummary>> getPublishedBlogs(Pageable pageable) {
        Page<BlogEntrySummary> summaries = blogService.getPublishedBlogs(pageable);
        return ResponseEntity.ok(summaries);
    }

    // --- Yeni Arama Endpoint'i ---
    @GetMapping("/search")
    public ResponseEntity<List<BlogEntrySummary>> search(@RequestParam String q) {
        return ResponseEntity.ok(blogService.searchBlogs(q));
    }

    @GetMapping("/read/{slug}")
    public ResponseEntity<BlogEntryResponse> getBlogBySlug(@PathVariable String slug) {
        BlogEntryResponse response = blogService.getBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BlogEntryResponse> createBlog(Principal principal,
                                                        @Valid @RequestBody BlogEntryRequest request) {
        BlogEntryResponse response = blogService.create(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BlogEntryResponse> updateBlog(Principal principal,
                                                        @PathVariable Long id,
                                                        @Valid @RequestBody BlogEntryRequest request) {
        BlogEntryResponse response = blogService.update(principal.getName(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBlog(Principal principal,
                                           @PathVariable Long id) {
        blogService.delete(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BlogEntrySummary>> getMyBlogs(Principal principal, Pageable pageable) {
        Page<BlogEntrySummary> summaries = blogService.getMyBlogs(principal.getName(), pageable);
        return ResponseEntity.ok(summaries);
    }
}