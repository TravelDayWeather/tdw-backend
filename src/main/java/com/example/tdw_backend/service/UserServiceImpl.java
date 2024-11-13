package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.payload.LoginRequest;
import com.example.tdw_backend.payload.LoginResponse;
import com.example.tdw_backend.payload.SignUpRequest;
import com.example.tdw_backend.repository.UserRepository;
import com.example.tdw_backend.security.JwtTokenProvider;
import com.example.tdw_backend.security.JwtTokenService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
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
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("user Login:: " + loginRequest.getEmail() + " " +  loginRequest.getPw());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + loginRequest.getEmail() + " not found"));

        if (!passwordEncoder.matches(loginRequest.getPw(), user.getPw())) {
            throw new RuntimeException("Invalid credentials");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPw()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(user);  // AccessToken 발급
        String refreshToken = jwtTokenProvider.createRefreshToken(user);
        System.out.println("token:: " + accessToken + " refreshToken:: " +  refreshToken);


        return new LoginResponse(user, accessToken, refreshToken);
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
