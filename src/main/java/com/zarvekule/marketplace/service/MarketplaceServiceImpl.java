package com.zarvekule.marketplace.service;

import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.marketplace.dto.ListingRequest;
import com.zarvekule.marketplace.dto.ListingResponse;
import com.zarvekule.marketplace.dto.SellerTrustDto;
import com.zarvekule.marketplace.entity.MarketplaceListing;
import com.zarvekule.marketplace.enums.ListingStatus;
import com.zarvekule.marketplace.enums.ProductCategory;
import com.zarvekule.marketplace.mapper.MarketplaceMapper;
import com.zarvekule.marketplace.repository.MarketplaceListingRepository;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketplaceServiceImpl implements MarketplaceService {

    private final MarketplaceListingRepository listingRepository;
    private final UserRepository userRepository;
    private final MarketplaceMapper listingMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    private final BlogEntryRepository blogRepository;
    private final HomebrewEntryRepository homebrewRepository;

    @Override
    @Transactional
    public ListingResponse create(String username, ListingRequest request) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));


        MarketplaceListing listing = new MarketplaceListing();
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setCategory(request.getCategory());
        listing.setCondition(request.getCondition());
        listing.setContactInfo(request.getContactInfo());
        listing.setImageUrl(request.getImageUrl());

        listing.setSeller(seller);
        listing.setStatus(ListingStatus.ACTIVE);

        listing.setExpiresAt(LocalDateTime.now().plusDays(30));

        listing = listingRepository.save(listing);
        return listingMapper.toResponse(listing);
    }

    @Override
    @Transactional
    public void markAsSold(String username, Long listingId) {
        MarketplaceListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ApiException("İlan bulunamadı.", HttpStatus.NOT_FOUND));

        if (!listing.getSeller().getUsername().equals(username)) {
            throw new ApiException("Yetkisiz işlem.", HttpStatus.FORBIDDEN);
        }

        listing.setStatus(ListingStatus.SOLD);
        listingRepository.save(listing);
    }

    @Override
    @Transactional
    public void delete(String username, Long listingId) {
        MarketplaceListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ApiException("İlan bulunamadı.", HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.UNAUTHORIZED));
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!listing.getSeller().getUsername().equals(username) && !isAdmin) {
            throw new ApiException("Yetkisiz işlem.", HttpStatus.FORBIDDEN);
        }

        listing.setStatus(ListingStatus.DELETED);
        listingRepository.save(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getActiveListings() {
        return listingMapper.toResponseList(
                listingRepository.findAllByStatusOrderByCreatedAtDesc(ListingStatus.ACTIVE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getActiveListingsByCategory(ProductCategory category) {
        return listingMapper.toResponseList(
                listingRepository.findAllByStatusAndCategoryOrderByCreatedAtDesc(ListingStatus.ACTIVE, category));
    }

    @Override
    public ListingResponse getById(Long id) {
        MarketplaceListing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ApiException("İlan bulunamadı.", HttpStatus.NOT_FOUND));
        return listingMapper.toResponse(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public SellerTrustDto getSellerTrustProfile(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ApiException("Satıcı bulunamadı.", HttpStatus.NOT_FOUND));

        SellerTrustDto trustDto = new SellerTrustDto();
        trustDto.setSeller(userMapper.toSummaryDto(seller));

        long months = ChronoUnit.MONTHS.between(seller.getCreatedAt(), LocalDateTime.now());
        trustDto.setAccountAgeMonths(months);

        long soldCount = listingRepository.countBySeller_IdAndStatus(sellerId, ListingStatus.SOLD);
        trustDto.setSuccessfulSales(soldCount);

        // 3. Topluluk Katkısı (Blog + Homebrew sayısı)
        // Not: Blog/Homebrew repository'lerinde 'countByAuthorId' metodu olması gerekir.
        // Şimdilik count() ile değilse list().size() ile (verimsiz ama basit) veya yeni repo metoduyla yapılabilir.
        // Hata vermemesi için şimdilik 0 veya varsa repo metodunu kullanın.
        // long blogCount = blogRepository.countByAuthor_Id(sellerId); // Repo'da tanımlanmalı
        // long hbCount = homebrewRepository.countByAuthor_Id(sellerId); // Repo'da tanımlanmalı
        // Şimdilik dummy:
        trustDto.setTotalContentEntries(0);

        return trustDto;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireOldListings() {
        LocalDateTime now = LocalDateTime.now();
        List<MarketplaceListing> expiredListings = listingRepository.findAllByStatusAndExpiresAtBefore(ListingStatus.ACTIVE, now);

        if (!expiredListings.isEmpty()) {
            log.info("{} adet ilan süresi dolduğu için EXPIRED statüsüne çekiliyor.", expiredListings.size());
            for (MarketplaceListing listing : expiredListings) {
                listing.setStatus(ListingStatus.EXPIRED);

                notificationService.createNotification(
                        listing.getSeller(),
                        "İlanının Süresi Doldu",
                        "'" + listing.getTitle() + "' başlıklı ilanın yayından kalktı. Tekrar yayınlamak için güncelleyebilirsin.",
                        NotificationType.MARKETPLACE_EXPIRED,
                        "/marketplace/" + listing.getId()
                );
            }
            listingRepository.saveAll(expiredListings);
        }
    }
}