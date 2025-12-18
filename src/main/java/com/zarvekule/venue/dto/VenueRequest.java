package com.zarvekule.venue.dto;

import com.zarvekule.venue.enums.VenueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VenueRequest {
    @NotBlank(message = "Mekan adı zorunludur.")
    private String name;

    private String description;

    @NotNull(message = "Mekan türü seçilmelidir.")
    private VenueType type;

    @NotBlank(message = "Adres zorunludur.")
    private String address;
    private String city;
    private String district;

    private double latitude;
    private double longitude;

    private String phone;
    private String website;
    private String instagramHandle;
}