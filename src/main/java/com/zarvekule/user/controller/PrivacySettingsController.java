package com.zarvekule.user.controller;

import com.zarvekule.user.dto.PrivacySettingsDto;
import com.zarvekule.user.service.PrivacySettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users/privacy-settings")
@RequiredArgsConstructor
public class PrivacySettingsController {

    private final PrivacySettingsService privacySettingsService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrivacySettingsDto> getMyPrivacySettings(Principal principal) {
        return ResponseEntity.ok(
                privacySettingsService.getPrivacySettings(principal.getName())
        );
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrivacySettingsDto> updateMyPrivacySettings(
            Principal principal,
            @RequestBody PrivacySettingsDto dto) {
        return ResponseEntity.ok(
                privacySettingsService.updatePrivacySettings(principal.getName(), dto)
        );
    }
}