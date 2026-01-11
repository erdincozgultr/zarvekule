package com.zarvekule.venue.repository;

import com.zarvekule.venue.entity.Venue;
import com.zarvekule.venue.enums.VenueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findAllByStatus(VenueStatus status);

    // MODERATION: Paginated status query
    Page<Venue> findAllByStatus(VenueStatus status, Pageable pageable);

    List<Venue> findAllByOwner_Id(Long ownerId);

    // MODERATION STATS: Status'a göre sayım
    long countByStatus(VenueStatus status);
}