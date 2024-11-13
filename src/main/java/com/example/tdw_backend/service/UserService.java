package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.payload.JwtAuthenticationResponse;
import com.example.tdw_backend.payload.LoginRequest;
import com.example.tdw_backend.payload.LoginResponse;
import com.example.tdw_backend.payload.SignUpRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    // 회원가입
    User signUp(SignUpRequest signUpRequest);

    // 로그인
    LoginResponse login(LoginRequest loginRequest);

    // 이메일 중복체크
    boolean validateEmail(String email);

    // 닉네임 중복체크
    boolean validateNickname(String nickname);
}
