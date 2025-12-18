package com.zarvekule.wiki.repository;

import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiEntryRepository extends JpaRepository<WikiEntry, Long> {

    WikiEntry findBySlug(String slug);

    List<WikiEntry> findAllByStatus(WikiStatus status);

    // Kategoriye göre getirme (Eksikti, eklendi)
    List<WikiEntry> findAllByStatusAndCategory(WikiStatus status, ContentCategory category);

    // Arama Sorgusu (Başlık veya içerikte arar)
    @Query("SELECT w FROM WikiEntry w WHERE w.status = 'PUBLISHED' AND " +
            "(LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(w.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<WikiEntry> searchPublic(@Param("keyword") String keyword);
}