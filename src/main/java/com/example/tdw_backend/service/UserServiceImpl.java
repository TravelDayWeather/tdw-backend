package com.example.tdw_backend.service;

import com.example.tdw_backend.dto.UserDto;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.model.LoginRequest;
import com.example.tdw_backend.model.SignUpRequest;
import com.example.tdw_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // 회원가입
    @Override
    public User signUp(SignUpRequest signUpRequest) {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .pw(passwordEncoder.encode(signUpRequest.getPw()))
                .name(signUpRequest.getName())
                .nickname(signUpRequest.getNickname())
                .phone(signUpRequest.getPhone())
                .build();

        return userRepository.save(user);
    }


    // 로그인
    @Override
    public User login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User with email "
                        + loginRequest.getEmail() + " not found"));

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
