package com.zarvekule.wiki.controller;

import com.zarvekule.wiki.dto.WikiCollectionDto;
import com.zarvekule.wiki.dto.WikiCollectionRequest;
import com.zarvekule.wiki.service.WikiCollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wiki-collections")
@RequiredArgsConstructor
public class WikiCollectionController {

    private final WikiCollectionService collectionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WikiCollectionDto> create(Principal principal,
                                                    @Valid @RequestBody WikiCollectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectionService.create(principal.getName(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(Principal principal, @PathVariable Long id) {
        collectionService.delete(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{collectionId}/wikis/{wikiId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addWiki(Principal principal,
                                        @PathVariable Long collectionId,
                                        @PathVariable Long wikiId) {
        collectionService.addEntryToCollection(principal.getName(), collectionId, wikiId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{collectionId}/wikis/{wikiId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeWiki(Principal principal,
                                           @PathVariable Long collectionId,
                                           @PathVariable Long wikiId) {
        collectionService.removeEntryFromCollection(principal.getName(), collectionId, wikiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WikiCollectionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.getById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WikiCollectionDto>> getMyCollections(Principal principal) {
        return ResponseEntity.ok(collectionService.getMyCollections(principal.getName()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WikiCollectionDto>> getUserPublicCollections(@PathVariable Long userId) {
        return ResponseEntity.ok(collectionService.getPublicCollectionsByUser(userId));
    }
}