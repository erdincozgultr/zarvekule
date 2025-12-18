package com.zarvekule.marketplace.service;

import com.zarvekule.marketplace.dto.ListingRequest;
import com.zarvekule.marketplace.dto.ListingResponse;
import com.zarvekule.marketplace.dto.SellerTrustDto;
import com.zarvekule.marketplace.enums.ProductCategory;
import java.util.List;

public interface MarketplaceService {
    ListingResponse create(String username, ListingRequest request);
    void markAsSold(String username, Long listingId);
    void delete(String username, Long listingId);

    List<ListingResponse> getActiveListings();
    List<ListingResponse> getActiveListingsByCategory(ProductCategory category);
    ListingResponse getById(Long id);

    SellerTrustDto getSellerTrustProfile(Long sellerId);
}