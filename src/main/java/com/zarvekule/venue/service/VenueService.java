package com.zarvekule.venue.service;

import com.zarvekule.venue.dto.ReviewRequest;
import com.zarvekule.venue.dto.VenueRequest;
import com.zarvekule.venue.dto.VenueResponse;

import java.util.List;

public interface VenueService {

    VenueResponse create(String username, VenueRequest request);

    List<VenueResponse> searchNearby(double lat, double lon, double maxDistanceKm);

    void claimVenue(String username, Long venueId, String reason);

    void approveClaim(Long venueId, Long userId);

    VenueResponse getById(Long id);

    List<VenueResponse> getAll();

    void addReview(String username, Long venueId, ReviewRequest request);

    // YENİ: Sadece yayınlanmış mekanları getiren metod
    List<VenueResponse> getPublishedVenues();
}