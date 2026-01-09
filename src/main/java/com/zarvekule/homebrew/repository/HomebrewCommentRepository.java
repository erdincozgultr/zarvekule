package com.zarvekule.homebrew.repository;

import com.zarvekule.homebrew.entity.HomebrewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomebrewCommentRepository extends JpaRepository<HomebrewComment, Long> {

    Page<HomebrewComment> findByHomebrew_IdAndIsApprovedTrue(Long homebrewId, Pageable pageable);

    Page<HomebrewComment> findByIsApprovedFalse(Pageable pageable);
}