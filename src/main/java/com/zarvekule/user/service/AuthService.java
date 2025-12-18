package com.zarvekule.user.service;

import com.zarvekule.user.dto.AuthResponseDto;
import com.zarvekule.user.dto.LoginRequestDto;
import com.zarvekule.user.dto.UserRequestDto;

public interface AuthService {

    AuthResponseDto register(UserRequestDto userRequestDto);

    AuthResponseDto login(LoginRequestDto loginRequestDto);
}