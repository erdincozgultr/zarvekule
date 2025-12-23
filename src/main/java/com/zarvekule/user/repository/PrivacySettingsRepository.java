package com.zarvekule.user.repository;

import com.zarvekule.user.entity.PrivacySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivacySettingsRepository extends JpaRepository<PrivacySettings, Long> {

    Optional<PrivacySettings> findByUser_Id(Long userId);

    Optional<PrivacySettings> findByUser_Username(String username);
}