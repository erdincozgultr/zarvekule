package com.zarvekule.wiki.repository;

import com.zarvekule.wiki.entity.WikiComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiCommentRepository extends JpaRepository<WikiComment, Long> {

    Page<WikiComment> findByWiki_IdAndIsApprovedTrue(Long wikiId, Pageable pageable);

    Page<WikiComment> findByIsApprovedFalse(Pageable pageable);
}