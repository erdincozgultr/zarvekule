package com.zarvekule.gamification.repository;

import com.zarvekule.gamification.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    Optional<UserStats> findByUser_Id(Long userId);

    List<UserStats> findAllByOrderByCurrentXpDesc();
}