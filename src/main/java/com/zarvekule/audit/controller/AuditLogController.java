package com.zarvekule.audit.controller;

import com.zarvekule.audit.entity.AuditLog;
import com.zarvekule.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/target")
    public ResponseEntity<List<AuditLog>> getLogsByTarget(@RequestParam String type, @RequestParam Long id) {
        return ResponseEntity.ok(auditLogRepository.findAllByTargetTypeAndTargetIdOrderByCreatedAtDesc(type, id));
    }
}