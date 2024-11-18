package com.example.tdw_backend.controller;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.repository.UserRepository;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.payload.JwtAuthenticationResponse;
import com.example.tdw_backend.payload.LoginRequest;
import com.example.tdw_backend.payload.SignUpRequest;
import com.example.tdw_backend.security.JwtTokenProvider;
import com.example.tdw_backend.security.JwtTokenService;
import com.example.tdw_backend.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // 회원가입
    @PostMapping("/api/signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpRequest signUpRequest) {
        if (signUpRequest == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userService.signUp(signUpRequest);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
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

            // 인증 성공 후 SecurityContext에 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Token 생성 및 저장
            Token token = jwtTokenService.loginOrRefreshToken(authentication);
            String accessToken = token.getAccessToken();
            String refreshToken = token.getRefreshToken();

            return ResponseEntity.ok(new JwtAuthenticationResponse(user.getUserId(), accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 비밀번호가 틀린 경우
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 기타 예외 처리
        }
    }


    // 이메일 중복체크
    @GetMapping("/api/users/validate-email")
    public ResponseEntity<Boolean> validateEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(userService.validateEmail(email));
    }

    // 닉네임 중복체크
    @GetMapping("/api/users/validate-nickname")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(userService.validateNickname(nickname));
    }

    // 마이페이지
    @GetMapping("/api/users/{userId}")
    public ResponseEntity<User> getMyPage(@PathVariable("userId") Long userId,
                                          @RequestHeader("Authorization") String authorization) {

        System.out.println("authorization: " + authorization);

        // Bearer 토큰 추출
        String token;
        try {
            token = jwtTokenProvider.getTokenFromAuthorizationHeader(authorization);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 토큰 만료 여부 검증
        if (jwtTokenProvider.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 토큰에서 이메일 추출
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String userEmail = claims.getSubject();

        // 이메일과 ID 일치 여부 검증
        Optional<User> user = userService.getMyPage(userId);
        if (user.isPresent() && user.get().getEmail().equals(userEmail)) {
            return ResponseEntity.ok(user.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
