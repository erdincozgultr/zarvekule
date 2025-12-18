package com.zarvekule.venue.controller;

import com.zarvekule.venue.dto.ClaimRequest;
import com.zarvekule.venue.dto.ReviewRequest;
import com.zarvekule.venue.dto.VenueRequest;
import com.zarvekule.venue.dto.VenueResponse;
import com.zarvekule.venue.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VenueResponse> create(Principal principal,
                                                @Valid @RequestBody VenueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(venueService.create(principal.getName(), request));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<VenueResponse>> searchNearby(@RequestParam double lat,
                                                            @RequestParam double lon,
                                                            @RequestParam(defaultValue = "20") double dist) {
        return ResponseEntity.ok(venueService.searchNearby(lat, lon, dist));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getById(id));
    }

    @PostMapping("/{id}/claim")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> claimVenue(Principal principal,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ClaimRequest request) {
        venueService.claimVenue(principal.getName(), id, request.getReason());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAll() {
        return ResponseEntity.ok(venueService.getAll());
    }

    @GetMapping("/public")
    public ResponseEntity<List<VenueResponse>> getPublicVenues() {
        return ResponseEntity.ok(venueService.getPublishedVenues());
    }

    @PostMapping("/{id}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addReview(Principal principal,
                                          @PathVariable Long id,
                                          @Valid @RequestBody ReviewRequest request) {
        venueService.addReview(principal.getName(), id, request);
        return ResponseEntity.ok().build();
    }
}