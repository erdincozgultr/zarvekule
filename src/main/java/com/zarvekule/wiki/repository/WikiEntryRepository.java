package com.zarvekule.wiki.repository;

import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiEntryRepository extends JpaRepository<WikiEntry, Long> {

    // === TEMEL SORGULAR ===

    Optional<WikiEntry> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySourceKey(String sourceKey);

    Optional<WikiEntry> findBySourceKey(String sourceKey);

    // === LİSTE SORGULARI (PAGINATION) ===

    Page<WikiEntry> findAllByStatus(WikiStatus status, Pageable pageable);

    Page<WikiEntry> findAllByStatusAndCategory(WikiStatus status, ContentCategory category, Pageable pageable);

    long countByStatusAndCategory(WikiStatus status, ContentCategory category);

    // === ARAMA SORGULARI (PAGINATION) ===

    @Query("SELECT w FROM WikiEntry w WHERE w.status = :status AND " +
            "LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<WikiEntry> searchByTitle(@Param("status") WikiStatus status,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);

    @Query("SELECT w FROM WikiEntry w WHERE w.status = :status AND w.category = :category AND " +
            "LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<WikiEntry> searchByCategoryAndTitle(@Param("status") WikiStatus status,
                                             @Param("category") ContentCategory category,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);

    // === TÜRKÇE ARAMA (JSONB) ===

    @Query(value = "SELECT * FROM wiki_entries w WHERE w.status = 'PUBLISHED' AND " +
            "w.turkish_content->>'name' ILIKE CONCAT('%', :keyword, '%')",
            countQuery = "SELECT COUNT(*) FROM wiki_entries w WHERE w.status = 'PUBLISHED' AND " +
                    "w.turkish_content->>'name' ILIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<WikiEntry> searchTurkishTitle(@Param("keyword") String keyword, Pageable pageable);

    // === İSTATİSTİK ===

    @Query("SELECT w.category, COUNT(w) FROM WikiEntry w WHERE w.status = 'PUBLISHED' GROUP BY w.category")
    List<Object[]> countByCategory();

    // === GÜNCELLEME ===

    @Modifying
    @Query("UPDATE WikiEntry w SET w.viewCount = w.viewCount + 1 WHERE w.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE WikiEntry w SET w.likeCount = w.likeCount + :delta WHERE w.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    // === ESKİ METODLAR (geriye uyumluluk) ===

    List<WikiEntry> findAllByStatus(WikiStatus status);

    List<WikiEntry> findAllByStatusAndCategory(WikiStatus status, ContentCategory category);

    @Query("SELECT w FROM WikiEntry w WHERE w.status = 'PUBLISHED' AND " +
            "(LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<WikiEntry> searchPublic(@Param("keyword") String keyword);
}
