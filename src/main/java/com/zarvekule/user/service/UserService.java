package com.zarvekule.user.service;

import com.zarvekule.user.dto.*;
import com.zarvekule.user.enums.ERole;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<UserResponseDto> findAll();

    UserResponseDto findById(Long id);

    UserResponseDto findByUsername(String username);

    UserResponseDto updateByPrincipal(String username, UserPatchRequestDto patchDto);

    void deleteByPrincipal(String username);

    UserSummaryDto getUserSummary(String username);

    UserProfileDto getUserProfile(String username);

    void addRoleToUser(String username, ERole roleName);

    void banUser(String adminUsername, Long userId, String reason);

    void unbanUser(String adminUsername, Long userId, String reason);

}