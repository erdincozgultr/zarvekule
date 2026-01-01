package com.zarvekule.blog.controller;

import com.zarvekule.blog.dto.BlogStatusUpdateRequest;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.mapper.BlogEntryMapper;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.user.entity.User;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogEntryController {

    private final BlogEntryService blogService;
    private final BlogEntryMapper blogEntryMapper;

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

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<BlogEntryResponse> updateBlogStatus(
            Principal principal,
            @PathVariable Long id,
            @RequestBody @Valid BlogStatusUpdateRequest request
    ) {
        // Blog'u bul
        BlogEntry blog = blogService.findById(id)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı: " + id, HttpStatus.NOT_FOUND));

        // Yetki kontrolü - Sadece kendi blogu veya admin
        User currentUser = ((UserDetailsImpl) ((Authentication) principal).getPrincipal()).getUser();
        boolean isOwner = blog.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Bu blogu güncelleyemezsiniz");
        }

        // Sadece status güncelle (moderasyon ATLA!)
        blog.setStatus(request.getStatus());

        // Kaydet
        BlogEntry updatedBlog = blogService.save(blog);

        // Response
        BlogEntryResponse response = blogEntryMapper.toResponseDto(updatedBlog);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BlogEntrySummary>> getMyBlogs(Principal principal, Pageable pageable) {
        Page<BlogEntrySummary> summaries = blogService.getMyBlogs(principal.getName(), pageable);
        return ResponseEntity.ok(summaries);
    }
}