package com.zarvekule.marketplace.repository;

import com.zarvekule.marketplace.entity.MarketplaceListing;
import com.zarvekule.marketplace.enums.ListingStatus;
import com.zarvekule.marketplace.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MarketplaceListingRepository extends JpaRepository<MarketplaceListing, Long> {

    List<MarketplaceListing> findAllByStatusOrderByCreatedAtDesc(ListingStatus status);

    List<MarketplaceListing> findAllByStatusAndCategoryOrderByCreatedAtDesc(ListingStatus status, ProductCategory category);

    List<MarketplaceListing> findAllBySeller_IdOrderByCreatedAtDesc(Long sellerId);

    long countBySeller_IdAndStatus(Long sellerId, ListingStatus status);

    List<MarketplaceListing> findAllByStatusAndExpiresAtBefore(ListingStatus status, LocalDateTime now);
}