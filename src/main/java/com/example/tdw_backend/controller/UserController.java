package com.example.tdw_backend.controller;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.payload.*;
import com.example.tdw_backend.repository.TokenRepository;
import com.example.tdw_backend.repository.UserRepository;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.security.JwtTokenProvider;
import com.example.tdw_backend.security.JwtTokenService;
import com.example.tdw_backend.service.AuthorizationService;
import com.example.tdw_backend.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private final AuthenticationManager authenticationManager;

    @Value("${app.jwtAccessExpirationInMs}")
    private Long jwtAccessExpirationInMs;

    @Value("${app.jwtRefreshExpirationInMs}")
    private Long jwtRefreshExpirationInMs;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TokenRepository tokenRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository, TokenRepository tokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpRequest signUpRequest) {
        if (signUpRequest == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userService.signUp(signUpRequest);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
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

    // refreshToken 생성
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            String refreshTokenRpl = refreshToken.replace("Bearer ", "");

            // 1. DB에서 리프레시 토큰 검증
            User user = tokenRepository.findByRefreshToken(refreshTokenRpl);

            // 2. 리프레시 토큰 유효성 확인
            if (jwtTokenProvider.isTokenExpired(refreshTokenRpl)) {

                // 3. 새 액세스 토큰 및 리프레시 토큰 생성
                String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail());
                String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

                // 4. DB에 리프레시 토큰 업데이트
                Token token = new Token();
                token.setAccessToken(newAccessToken);
                token.setAccessTokenExpiryDate(new Date(System.currentTimeMillis() + jwtAccessExpirationInMs).toInstant());
                token.setRefreshToken(newRefreshToken);
                token.setRefreshTokenExpiryDate(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs).toInstant());
                tokenRepository.save(token);

                // 5. 새로운 토큰 반환
                Map<String, String> tokens = Map.of(
                        "accessToken", newAccessToken,
                        "refreshToken", newRefreshToken
                );
                return ResponseEntity.ok(tokens);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error refreshing token");
        }
    }

    // token 인증
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    // 이메일 중복체크
    @GetMapping("/users/validate-email")
    public ResponseEntity<Boolean> validateEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(userService.validateEmail(email));
    }

    // 닉네임 중복체크
    @GetMapping("/users/validate-nickname")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(userService.validateNickname(nickname));
    }

    // 마이페이지
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getMyPage(@PathVariable("userId") Long userId,
                                          @RequestHeader("Authorization") String authorization) {
        String token = authorizationService.extractToken(authorization);
        authorizationService.validateToken(token);

        String userEmail = authorizationService.getUserEmailFromToken(token);
        Optional<User> user = userService.getMyPage(userId);

        if (user.isPresent() && user.get().getEmail().equals(userEmail)) {
            return ResponseEntity.ok(user.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 마이페이지 수정
    @PatchMapping("/users/{userId}")
    public ResponseEntity<User> updateMyPage(@PathVariable("userId") Long userId,
                                             @RequestBody UserUpdateRequest userUpdateRequest,
                                             @RequestHeader("Authorization") String authorization) {
        String token = authorizationService.extractToken(authorization);
        authorizationService.validateToken(token);

        User updatedUser = userService.updateMyPage(userId, userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        String token;
        try {
            token = JwtTokenProvider.getTokenFromAuthorizationHeader(authorization);

            jwtTokenService.isTokenInvalidated(token);

            // 토큰이 만료된 경우, 바로 로그아웃 처리
            if (jwtTokenProvider.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 만료되었습니다. 다시 로그인해주세요.");
            }

            jwtTokenService.invalidateToken(token);
            return ResponseEntity.ok("로그아웃 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
        }
    }
}
