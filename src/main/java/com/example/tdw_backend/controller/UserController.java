package com.example.tdw_backend.controller;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.repository.TokenRepository;
import com.example.tdw_backend.repository.UserRepository;
import com.example.tdw_backend.security.JwtTokenProvider;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.payload.JwtAuthenticationResponse;
import com.example.tdw_backend.payload.LoginRequest;
import com.example.tdw_backend.payload.SignUpRequest;
import com.example.tdw_backend.security.JwtTokenValidator;
import com.example.tdw_backend.security.JwtTokenService;
import com.example.tdw_backend.service.UserService;
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

            // Token 생성 (AccessToken이 포함된 Token 객체를 반환)
            Token token = jwtTokenService.loginOrRefreshToken(authentication);  // 로그인 또는 토큰 갱신
            String accessToken = token.getAccessToken();  // accessToken 가져오기
            String refreshToken = token.getRefreshToken();  // refreshToken 가져오기

            // 응답으로 JWT 포함
            return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));

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
}
