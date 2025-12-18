package com.zarvekule.user.controller;

import com.zarvekule.user.dto.*;
import com.zarvekule.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/summary/{username}")
    public ResponseEntity<UserSummaryDto> getUserSummary(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserSummary(username));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }


    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyProfile(Principal principal,
                                                           @Valid @RequestBody UserPatchRequestDto patchDto) {
        return ResponseEntity.ok(userService.updateByPrincipal(principal.getName(), patchDto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Principal principal) {
        userService.deleteByPrincipal(principal.getName());
        return ResponseEntity.noContent().build();
    }
}