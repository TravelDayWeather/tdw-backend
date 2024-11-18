package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.payload.SignUpRequest;
import com.example.tdw_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional<User> getMyPage(Long userId) {
        return Optional.ofNullable(userRepository.findByUserId(userId));

    }
}
