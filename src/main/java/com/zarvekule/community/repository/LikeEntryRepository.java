package com.zarvekule.community.repository;

import com.zarvekule.community.entity.LikeEntry;
import com.zarvekule.community.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntryRepository extends JpaRepository<LikeEntry, Long> {

    // Listeleme ve kontrol için kullandığımız boolean metod (kalsın)
    boolean existsByTargetTypeAndTargetIdAndUser_Id(TargetType targetType, Long targetId, Long userId);

    // Toggle (beğeniyi kaldırma) işlemi için nesnenin kendisini bulan metod (EKLE)
    Optional<LikeEntry> findByUser_IdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    @Query("SELECT l.targetId FROM LikeEntry l WHERE l.user.id = :userId AND l.targetType = :targetType AND l.targetId IN :ids")
    List<Long> findLikedIdsByUser(@Param("userId") Long userId, @Param("targetType") TargetType targetType, @Param("ids") List<Long> ids);

    long countByTargetTypeAndTargetId(TargetType targetType, Long targetId);
}