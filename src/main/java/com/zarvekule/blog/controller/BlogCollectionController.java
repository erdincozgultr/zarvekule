package com.zarvekule.blog.controller;

import com.zarvekule.blog.dto.BlogCollectionDto;
import com.zarvekule.blog.dto.BlogCollectionRequest;
import com.zarvekule.blog.service.BlogCollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/blog-collections")
@RequiredArgsConstructor
public class BlogCollectionController {

    private final BlogCollectionService collectionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BlogCollectionDto> create(Principal principal,
                                                    @Valid @RequestBody BlogCollectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectionService.create(principal.getName(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(Principal principal, @PathVariable Long id) {
        collectionService.delete(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{collectionId}/blogs/{blogId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addBlog(Principal principal,
                                        @PathVariable Long collectionId,
                                        @PathVariable Long blogId) {
        collectionService.addEntryToCollection(principal.getName(), collectionId, blogId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{collectionId}/blogs/{blogId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeBlog(Principal principal,
                                           @PathVariable Long collectionId,
                                           @PathVariable Long blogId) {
        collectionService.removeEntryFromCollection(principal.getName(), collectionId, blogId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogCollectionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.getById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BlogCollectionDto>> getMyCollections(Principal principal) {
        return ResponseEntity.ok(collectionService.getMyCollections(principal.getName()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BlogCollectionDto>> getUserPublicCollections(@PathVariable Long userId) {
        return ResponseEntity.ok(collectionService.getPublicCollectionsByUser(userId));
    }
}