package com.zarvekule.venue.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.venue.enums.VenueStatus;
import com.zarvekule.venue.enums.VenueType;
import lombok.Data;

@Data
public class VenueResponse {
    private Long id;
    private String name;
    private String description;
    private VenueType type;
    private String address;
    private String city;
    private String district;
    private double latitude;
    private double longitude;
    private String phone;
    private String website;
    private String instagramHandle;
    private VenueStatus status;
    private double averageRating;
    private int reviewCount;
    private UserSummaryDto owner;

    private Double distanceKm;
}