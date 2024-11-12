package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.model.LoginRequest;
import com.example.tdw_backend.model.SignUpRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    // 회원가입
    User signUp(SignUpRequest signUpRequest);

    // 로그인
    User login(LoginRequest loginRequest);

    // 이메일 중복체크
    boolean validateEmail(String email);

    // 닉네임 중복체크
    boolean validateNickname(String nickname);
}
