package com.zarvekule.user.controller;

import com.zarvekule.user.dto.AuthResponseDto;
import com.zarvekule.user.dto.LoginRequestDto;
import com.zarvekule.user.dto.UserRequestDto;
import com.zarvekule.user.dto.UserResponseDto;
import com.zarvekule.user.service.AuthService;
import com.zarvekule.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser(Principal principal) {
        UserResponseDto user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }
}