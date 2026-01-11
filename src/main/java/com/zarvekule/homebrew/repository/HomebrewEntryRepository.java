package com.zarvekule.homebrew.repository;

import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HomebrewEntryRepository extends JpaRepository<HomebrewEntry, Long> {

    // ============================================
    // DETAY SORGULARI
    // ============================================

    Optional<HomebrewEntry> findBySlugAndStatus(String slug, HomebrewStatus status);

    boolean existsBySlug(String slug);

    // ============================================
    // LİSTELEME - SİMPLE (Mevcut metotlar)
    // ============================================

    List<HomebrewEntry> findAllByStatusOrderByPublishedAtDesc(HomebrewStatus status);

    List<HomebrewEntry> findAllByStatusAndCategoryOrderByPublishedAtDesc(
            HomebrewStatus status,
            HomebrewCategory category
    );

    Optional<HomebrewEntry> findBySlug(String slug);

    long countByStatus(HomebrewStatus status);

    List<HomebrewEntry> findAllByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // ============================================
    // LİSTELEME - PAGINATION (YENİ!)
    // ============================================

    /**
     * Tüm homebrew'lar (sayfalama ile)
     */
    Page<HomebrewEntry> findAllByStatus(HomebrewStatus status, Pageable pageable);

    /**
     * Kategoriye göre homebrew'lar (sayfalama ile)
     */
    Page<HomebrewEntry> findAllByStatusAndCategory(
            HomebrewStatus status,
            HomebrewCategory category,
            Pageable pageable
    );

    /**
     * Kullanıcının yayınlanmış homebrew'ları (user profile için)
     */
    List<HomebrewEntry> findAllByAuthorIdAndStatusOrderByPublishedAtDesc(
            Long authorId,
            HomebrewStatus status
    );

    // ============================================
    // ARAMA SORGULARI
    // ============================================

    /**
     * Arama (simple - list)
     */
    @Query("SELECT h FROM HomebrewEntry h WHERE h.status = 'PUBLISHED' AND " +
            "(LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(h.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<HomebrewEntry> searchPublic(@Param("query") String query);

    /**
     * Arama (pagination - YENİ!)
     */
    @Query("SELECT h FROM HomebrewEntry h WHERE h.status = 'PUBLISHED' AND " +
            "(LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(h.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<HomebrewEntry> searchPublicPaginated(@Param("query") String query, Pageable pageable);


    /**
     * Kategori bazlı arama (pagination - YENİ!)
     */
    @Query("SELECT h FROM HomebrewEntry h WHERE h.status = 'PUBLISHED' " +
            "AND h.category = :category " +
            "AND (LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(h.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<HomebrewEntry> searchByCategoryPaginated(
            @Param("category") HomebrewCategory category,
            @Param("query") String query,
            Pageable pageable
    );

    // ============================================
    // İSTATİSTİKLER
    // ============================================

    long countByStatusAndCategory(HomebrewStatus status, HomebrewCategory category);

    // Lonca üyelerinin homebrew'larını say
    long countByAuthorIdInAndCreatedAtAfter(List<Long> authorIds, LocalDateTime createdAt);

    // Tek üyenin homebrew'larını say
    long countByAuthorIdAndCreatedAtAfter(Long authorId, LocalDateTime createdAt);

    // Son 10 homebrew
    List<HomebrewEntry> findTop10ByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);
}