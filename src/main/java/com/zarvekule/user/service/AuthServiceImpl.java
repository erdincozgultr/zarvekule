package com.zarvekule.user.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.service.GamificationService;
import com.zarvekule.security.JwtTokenProvider;
import com.zarvekule.user.dto.AuthResponseDto;
import com.zarvekule.user.dto.LoginRequestDto;
import com.zarvekule.user.dto.UserRequestDto;
import com.zarvekule.user.dto.UserResponseDto;
import com.zarvekule.user.entity.Role;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.enums.ERole;
import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.user.repository.RoleRepository;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final GamificationService gamificationService;

    @Override
    @Transactional
    public AuthResponseDto register(UserRequestDto userRequestDto) {

        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new ApiException("Bu kullanıcı adı zaten alınmış!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ApiException("Bu e-posta adresi zaten kullanımda!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        user.setTitle("Gezgin");
        user.setDisplayName(userRequestDto.getUsername());

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new ApiException( "Sistem Hatası: ROLE_USER bulunamadı.", HttpStatus.INTERNAL_SERVER_ERROR));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);
        UserResponseDto userResponse = userMapper.toResponseDto(user);

        gamificationService.initStats(user);
        return new AuthResponseDto(token, userResponse);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(),
                        loginRequestDto.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user);

        UserResponseDto userResponse = userMapper.toResponseDto(user);

        return new AuthResponseDto(token, userResponse);
    }
}