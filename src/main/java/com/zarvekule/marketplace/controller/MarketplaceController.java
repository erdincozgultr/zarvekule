package com.zarvekule.marketplace.controller;

import com.zarvekule.marketplace.dto.ListingRequest;
import com.zarvekule.marketplace.dto.ListingResponse;
import com.zarvekule.marketplace.dto.SellerTrustDto;
import com.zarvekule.marketplace.enums.ProductCategory;
import com.zarvekule.marketplace.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ListingResponse> create(Principal principal,
                                                  @Valid @RequestBody ListingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(marketplaceService.create(principal.getName(), request));
    }

    @PatchMapping("/{id}/sold")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsSold(Principal principal, @PathVariable Long id) {
        marketplaceService.markAsSold(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(Principal principal, @PathVariable Long id) {
        marketplaceService.delete(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ListingResponse>> getActiveListings() {
        return ResponseEntity.ok(marketplaceService.getActiveListings());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ListingResponse>> getByCategory(@PathVariable ProductCategory category) {
        return ResponseEntity.ok(marketplaceService.getActiveListingsByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(marketplaceService.getById(id));
    }

    @GetMapping("/seller-trust/{sellerId}")
    public ResponseEntity<SellerTrustDto> getSellerTrustProfile(@PathVariable Long sellerId) {
        return ResponseEntity.ok(marketplaceService.getSellerTrustProfile(sellerId));
    }
}