package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.payload.SignUpRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    // 회원가입
    User signUp(SignUpRequest signUpRequest);

    // 이메일 중복체크
    boolean validateEmail(String email);

    // 닉네임 중복체크
    boolean validateNickname(String nickname);

    // 마이페이지
    Optional<User> getMyPage(Long userId);
}
