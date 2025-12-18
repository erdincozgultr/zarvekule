package com.zarvekule.audit.service;

import com.zarvekule.audit.entity.AuditLog;
import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.repository.AuditLogRepository;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logAction(String username, AuditAction action, String targetType, Long targetId, String details) {

        User actor = userRepository.findByUsername(username).orElse(null);

        AuditLog log = new AuditLog();
        if (actor != null) {
            log.setActorId(actor.getId());
            log.setActorUsername(actor.getUsername());
        } else {
            log.setActorId(0L);
            log.setActorUsername(username != null ? username : "SYSTEM");
        }

        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetails(details);
        log.setIpAddress(getClientIp());
        log.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(log);
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0];
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
        }
        return "UNKNOWN";
    }
}