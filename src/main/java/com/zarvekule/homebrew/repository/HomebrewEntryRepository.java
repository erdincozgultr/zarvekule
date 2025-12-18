package com.zarvekule.homebrew.repository;

import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomebrewEntryRepository extends JpaRepository<HomebrewEntry, Long> {

    Optional<HomebrewEntry> findBySlugAndStatus(String slug, HomebrewStatus status);

    // Boolean dönüş tipi hatasını önlemek için existsBy kullanıyoruz
    boolean existsBySlug(String slug);

    List<HomebrewEntry> findAllByStatusOrderByPublishedAtDesc(HomebrewStatus status);

    List<HomebrewEntry> findAllByStatusAndCategoryOrderByPublishedAtDesc(HomebrewStatus status, HomebrewCategory category);

    List<HomebrewEntry> findAllByAuthorIdOrderByCreatedAtDesc(Long authorId);

    long countByStatusAndCategory(HomebrewStatus status, HomebrewCategory category);

    // Arama Sorgusu (İsim, açıklama veya detayda arar)
    @Query("SELECT h FROM HomebrewEntry h WHERE h.status = 'PUBLISHED' AND " +
            "(LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(h.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(h.excerpt) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<HomebrewEntry> searchPublic(@Param("query") String query);
}