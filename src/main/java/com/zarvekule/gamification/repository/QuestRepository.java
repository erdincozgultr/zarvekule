package com.zarvekule.gamification.repository;

import com.zarvekule.gamification.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByGuildIdAndCompletedFalseOrderByDeadlineAsc(Long guildId);

    List<Quest> findByGuildIdOrderByCreatedAtDesc(Long guildId);

    List<Quest> findByDeadlineBeforeAndCompletedFalse(LocalDateTime deadline);
}



