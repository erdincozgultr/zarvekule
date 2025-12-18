package com.zarvekule.blog.repository;

import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.enums.BlogStatus;
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
public interface BlogEntryRepository extends JpaRepository<BlogEntry, Long> {

    Page<BlogEntry> findByStatusOrderByPublishedAtDesc(BlogStatus status, Pageable pageable);

    Optional<BlogEntry> findBySlugAndStatus(String slug, BlogStatus status);

    Page<BlogEntry> findByAuthor_UsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    Page<BlogEntry> findByAuthor_UsernameAndStatusOrderByPublishedAtDesc(String username, BlogStatus status, Pageable pageable);

    Optional<BlogEntry> findByIdAndAuthor_Id(Long id, Long authorId);

    List<BlogEntry> findAllByStatusAndPublishedAtBefore(BlogStatus status, LocalDateTime dateTime);

    @Query("SELECT b FROM BlogEntry b WHERE b.status = 'PUBLISHED' AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<BlogEntry> searchPublic(@Param("query") String query);
}