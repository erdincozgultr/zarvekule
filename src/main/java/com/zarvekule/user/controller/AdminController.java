package com.zarvekule.user.controller;

import com.zarvekule.gamification.service.GamificationService;
import com.zarvekule.user.dto.BanRequest;
import com.zarvekule.user.entity.Role;
import com.zarvekule.user.enums.ERole;
import com.zarvekule.user.service.RoleService;
import com.zarvekule.user.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final GamificationService gamificationService;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRoleToUser(@RequestBody RoleAssignmentRequest request) {
        userService.addRoleToUser(request.getUsername(), request.getRole());
        return ResponseEntity.ok("Rol başarıyla atandı: " + request.getRole());
    }

    @Data
    public static class RoleAssignmentRequest {
        private String username;
        private ERole role;
    }

    @PostMapping("/users/{id}/ban")
    public ResponseEntity<Void> banUser(Principal principal,
                                        @PathVariable Long id,
                                        @Valid @RequestBody BanRequest request) {
        userService.banUser(principal.getName(), id, request.getReason());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/unban")
    public ResponseEntity<Void> unbanUser(Principal principal,
                                          @PathVariable Long id,
                                          @Valid @RequestBody BanRequest request) {
        userService.unbanUser(principal.getName(), id, request.getReason());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/badge")
    public ResponseEntity<Void> giveBadge(Principal principal,
                                          @PathVariable Long userId,
                                          @RequestParam String code) {
        gamificationService.assignBadgeToUser(principal.getName(), userId, code);
        return ResponseEntity.ok().build();
    }
}