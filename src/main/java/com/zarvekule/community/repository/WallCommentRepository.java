package com.zarvekule.community.repository;

import com.zarvekule.community.entity.WallComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WallCommentRepository extends JpaRepository<WallComment, Long> {

    Page<WallComment> findByProfileOwner_IdOrderByCreatedAtDesc(Long profileOwnerId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(
            "SELECT wc FROM WallComment wc " +
                    "JOIN FETCH wc.author a " +
                    "JOIN FETCH wc.profileOwner po " +
                    "WHERE wc.id = :id"
    )
    Optional<WallComment> findByIdWithAuthorAndOwner(Long id);
}