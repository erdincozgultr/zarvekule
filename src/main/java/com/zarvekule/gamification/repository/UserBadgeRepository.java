package com.zarvekule.gamification.repository;

import com.zarvekule.gamification.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findAllByUser_Id(Long userId);

    boolean existsByUser_IdAndBadge_ConditionCode(Long userId, String conditionCode);

    Optional<UserBadge> findByUser_IdAndBadge_Id(Long userId, Long badgeId);

    /**
     * ✅ YENİ: Kullanıcının toplam rozet sayısı
     */
    int countByUser_Id(Long userId);
}