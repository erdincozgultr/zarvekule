package com.zarvekule.homebrew.controller;

import com.zarvekule.homebrew.dto.CollectionDto;
import com.zarvekule.homebrew.dto.CollectionRequest;
import com.zarvekule.homebrew.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CollectionDto> create(Principal principal,
                                                @Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectionService.create(principal.getName(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(Principal principal, @PathVariable Long id) {
        collectionService.delete(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{collectionId}/entries/{entryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addEntry(Principal principal,
                                         @PathVariable Long collectionId,
                                         @PathVariable Long entryId) {
        collectionService.addEntryToCollection(principal.getName(), collectionId, entryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{collectionId}/entries/{entryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeEntry(Principal principal,
                                            @PathVariable Long collectionId,
                                            @PathVariable Long entryId) {
        collectionService.removeEntryFromCollection(principal.getName(), collectionId, entryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.getById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CollectionDto>> getMyCollections(Principal principal) {
        return ResponseEntity.ok(collectionService.getMyCollections(principal.getName()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CollectionDto>> getUserPublicCollections(@PathVariable Long userId) {
        return ResponseEntity.ok(collectionService.getPublicCollectionsByUser(userId));
    }
}