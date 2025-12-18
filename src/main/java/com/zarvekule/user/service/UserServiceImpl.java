package com.zarvekule.user.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.user.dto.*;
import com.zarvekule.user.entity.Role;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.enums.ERole;
import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.user.repository.RoleRepository;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuditService auditService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    @Override
    public UserSummaryDto getUserSummary(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı: " + username, HttpStatus.NOT_FOUND));

        return new UserSummaryDto(
                user.getUsername(),
                user.getDisplayName() != null ? user.getDisplayName() : user.getUsername(),
                user.getAvatarUrl(),
                user.getTitle() != null ? user.getTitle() : "Gezgin"
        );
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı: " + username, HttpStatus.NOT_FOUND));

        UserProfileDto profile = new UserProfileDto();
        profile.setUsername(user.getUsername());
        profile.setDisplayName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        profile.setBio(user.getBio());
        profile.setAvatarUrl(user.getAvatarUrl());
        profile.setBannerUrl(user.getBannerUrl());
        profile.setTitle(user.getTitle() != null ? user.getTitle() : "Gezgin");
        profile.setJoinedAt(user.getCreatedAt());

        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
        profile.setRoles(roleNames);

        return profile;
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("ID " + id + " ile kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateByPrincipal(String username, UserPatchRequestDto patchDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı: " + username, HttpStatus.NOT_FOUND));

        if (patchDto.displayName() != null && !patchDto.displayName().isBlank()) {
            user.setDisplayName(patchDto.displayName());
        }

        if (patchDto.bio() != null) {
            user.setBio(patchDto.bio());
        }

        if (patchDto.newPassword() != null && !patchDto.newPassword().isBlank()) {
            if (passwordEncoder.matches(patchDto.newPassword(), user.getPassword())) {
                throw new ApiException("Yeni şifre, mevcut şifrenizle aynı olamaz.", HttpStatus.BAD_REQUEST);
            }
            user.setPassword(passwordEncoder.encode(patchDto.newPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public void deleteByPrincipal(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı: " + username, HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void addRoleToUser(String username, ERole roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı: " + username, HttpStatus.NOT_FOUND));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ApiException("Rol bulunamadı: " + roleName, HttpStatus.NOT_FOUND));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void banUser(String adminUsername, Long userId, String reason) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        User adminUser = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ApiException("Admin bulunamadı.", HttpStatus.NOT_FOUND));

        if (targetUser.getId().equals(adminUser.getId())) {
            throw new ApiException("Kendinizi banlayamazsınız.", HttpStatus.BAD_REQUEST);
        }

        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
        if (isTargetAdmin) {
            throw new ApiException("Yönetici rolündeki bir kullanıcı banlanamaz.", HttpStatus.FORBIDDEN);
        }

        if (targetUser.isBanned()) {
            throw new ApiException("Kullanıcı zaten yasaklı.", HttpStatus.BAD_REQUEST);
        }

        targetUser.setBanned(true);
        userRepository.save(targetUser);

        auditService.logAction(
                adminUsername,
                AuditAction.USER_BAN,
                "USER",
                userId,
                "Kullanıcı banlandı. Sebep: " + reason
        );
    }

    @Override
    @Transactional
    public void unbanUser(String adminUsername, Long userId, String reason) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        if (!targetUser.isBanned()) {
            throw new ApiException("Kullanıcı zaten yasaklı değil.", HttpStatus.BAD_REQUEST);
        }

        targetUser.setBanned(false);
        userRepository.save(targetUser);

        auditService.logAction(
                adminUsername,
                AuditAction.USER_UNBAN,
                "USER",
                userId,
                "Kullanıcı banı kaldırıldı. Sebep: " + reason
        );
    }
}