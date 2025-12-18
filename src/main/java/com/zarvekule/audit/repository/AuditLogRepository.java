package com.zarvekule.audit.repository;

import com.zarvekule.audit.entity.AuditLog;
import com.zarvekule.audit.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByActorIdOrderByCreatedAtDesc(Long actorId);

    List<AuditLog> findAllByActionOrderByCreatedAtDesc(AuditAction action);

    List<AuditLog> findAllByTargetTypeAndTargetIdOrderByCreatedAtDesc(String targetType, Long targetId);
}