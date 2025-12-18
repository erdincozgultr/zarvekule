package com.zarvekule.venue.repository;

import com.zarvekule.venue.entity.VenueReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueReviewRepository extends JpaRepository<VenueReview, Long> {
    List<VenueReview> findByVenueId(Long venueId);

    boolean existsByVenueIdAndUserId(Long venueId, Long userId); // Bir kişi bir mekana tek yorum atabilsin diye
}