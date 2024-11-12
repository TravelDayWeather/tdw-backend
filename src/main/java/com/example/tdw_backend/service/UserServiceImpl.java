package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.model.LoginRequest;
import com.example.tdw_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // 회원가입
    @Override
    public User signUp(User user) {
        return userRepository.save(user);
    }


    // 로그인
    @Override
    public User login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPw(), user.getPw())) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }

    // 이메일 중복체크
    @Override
    public boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복체크
    @Override
    public boolean validateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
