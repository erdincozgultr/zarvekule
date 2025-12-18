package com.zarvekule.notification.repository;

import com.zarvekule.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByRecipient_IdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipient_IdAndIsReadFalse(Long recipientId);

    List<Notification> findAllByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);
}