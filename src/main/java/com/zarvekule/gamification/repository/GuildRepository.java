package com.zarvekule.gamification.repository;

import com.zarvekule.gamification.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {

    /**
     * Guild'i üyelerle birlikte getir (N+1 problem önleme)
     */
    @Query("SELECT g FROM Guild g LEFT JOIN FETCH g.members WHERE g.id = :id")
    Optional<Guild> findByIdWithMembers(@Param("id") Long id);

    /**
     * Kullanıcının guild'ini bul
     */
    @Query("SELECT g FROM Guild g JOIN g.members m WHERE m.id = :userId")
    Optional<Guild> findByMemberId(@Param("userId") Long userId);

    /**
     * İsme göre guild bul (unique olduğu için)
     */
    Optional<Guild> findByName(String name);

    /**
     * İsmin kullanılıp kullanılmadığını kontrol et
     */
    boolean existsByName(String name);
}