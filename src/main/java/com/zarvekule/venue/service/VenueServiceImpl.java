package com.zarvekule.venue.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.venue.dto.ReviewRequest;
import com.zarvekule.venue.dto.VenueRequest;
import com.zarvekule.venue.dto.VenueResponse;
import com.zarvekule.venue.entity.Venue;
import com.zarvekule.venue.entity.VenueReview;
import com.zarvekule.venue.enums.VenueStatus;
import com.zarvekule.venue.mapper.VenueMapper;
import com.zarvekule.venue.repository.VenueRepository;
import com.zarvekule.venue.repository.VenueReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final VenueMapper venueMapper;
    private final NotificationService notificationService;
    private final VenueReviewRepository reviewRepository;

    @Override
    @Transactional
    public VenueResponse create(String username, VenueRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        Venue venue = new Venue();
        venue.setName(request.getName());
        venue.setDescription(request.getDescription());
        venue.setType(request.getType());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setDistrict(request.getDistrict());
        venue.setLatitude(request.getLatitude());
        venue.setLongitude(request.getLongitude());
        venue.setPhone(request.getPhone());
        venue.setWebsite(request.getWebsite());
        venue.setInstagramHandle(request.getInstagramHandle());

        venue.setCreatedBy(user);
        venue.setStatus(VenueStatus.PENDING_APPROVAL);
        venue.setCreatedAt(LocalDateTime.now());

        venue = venueRepository.save(venue);
        return venueMapper.toResponse(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> searchNearby(double userLat, double userLon, double maxDistanceKm) {
        List<Venue> allVenues = venueRepository.findAllByStatus(VenueStatus.PUBLISHED);
        List<VenueResponse> nearbyVenues = new ArrayList<>();

        for (Venue venue : allVenues) {
            double distance = calculateDistance(userLat, userLon, venue.getLatitude(), venue.getLongitude());
            if (distance <= maxDistanceKm) {
                nearbyVenues.add(venueMapper.toResponse(venue, distance));
            }
        }

        nearbyVenues.sort((v1, v2) -> Double.compare(v1.getDistanceKm(), v2.getDistanceKm()));

        return nearbyVenues;
    }

    @Override
    @Transactional
    public void claimVenue(String username, Long venueId, String reason) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ApiException("Mekan bulunamadı.", HttpStatus.NOT_FOUND));

        if (venue.getOwner() != null) {
            throw new ApiException("Bu mekanın zaten bir sahibi var.", HttpStatus.CONFLICT);
        }

        // Açıklamayı kaydet ve statüyü güncelle
        venue.setClaimReason(reason);
        venue.setStatus(VenueStatus.CLAIM_PENDING);
        venueRepository.save(venue);

        // Buraya Admin'e bildirim gönderme kodu eklenebilir.
    }

    @Override
    @Transactional
    public void approveClaim(Long venueId, Long userId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ApiException("Mekan bulunamadı.", HttpStatus.NOT_FOUND));

        User newOwner = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        venue.setOwner(newOwner);
        venue.setStatus(VenueStatus.PUBLISHED);
        venueRepository.save(venue);

        notificationService.createNotification(
                newOwner,
                "Mekan Sahipliği Onaylandı! 🏠",
                "'" + venue.getName() + "' mekanı artık senin hesabına tanımlandı.",
                NotificationType.CONTENT_APPROVED,
                "/venues/" + venue.getId()
        );
    }

    @Override
    public VenueResponse getById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ApiException("Mekan bulunamadı.", HttpStatus.NOT_FOUND));
        return venueMapper.toResponse(venue);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Kilometre

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    @Override
    public List<VenueResponse> getAll() {
        return venueRepository.findAll().stream()
                .map(venueMapper::toResponse) // Mapper'ın tek parametreli metodunu kullanıyoruz
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> getPublishedVenues() {
        // Sadece PUBLISHED (Yayında) olanları getir
        return venueRepository.findAllByStatus(VenueStatus.PUBLISHED).stream()
                .map(venueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addReview(String username, Long venueId, ReviewRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ApiException("Mekan bulunamadı.", HttpStatus.NOT_FOUND));

        // Kullanıcı daha önce yorum yapmış mı?
        if (reviewRepository.existsByVenueIdAndUserId(venueId, user.getId())) {
            throw new ApiException("Bu mekan için zaten bir yorumunuz var.", HttpStatus.CONFLICT);
        }

        // Yorumu kaydet
        VenueReview review = new VenueReview();
        review.setVenue(venue);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        reviewRepository.save(review);

        // Mekan istatistiklerini güncelle (Ortalama hesapla)
        updateVenueRating(venue);
    }

    private void updateVenueRating(Venue venue) {
        List<VenueReview> reviews = reviewRepository.findByVenueId(venue.getId());

        double average = reviews.stream()
                .mapToInt(VenueReview::getRating)
                .average()
                .orElse(0.0);

        venue.setAverageRating(Math.round(average * 10.0) / 10.0); // Virgülden sonra 1 hane
        venue.setReviewCount(reviews.size());

        venueRepository.save(venue);
    }
}