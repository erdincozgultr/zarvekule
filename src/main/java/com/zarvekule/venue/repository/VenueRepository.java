package com.zarvekule.venue.repository;

import com.zarvekule.venue.entity.Venue;
import com.zarvekule.venue.enums.VenueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findAllByStatus(VenueStatus status);

    List<Venue> findAllByOwner_Id(Long ownerId);
}