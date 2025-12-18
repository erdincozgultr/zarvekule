package com.zarvekule.gamification.repository;

import com.zarvekule.gamification.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findAllByUser_Id(Long userId);

    boolean existsByUser_IdAndBadge_ConditionCode(Long userId, String conditionCode);
}