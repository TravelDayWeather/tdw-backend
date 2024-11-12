package com.example.tdw_backend.controller;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.model.LoginRequest;
import com.example.tdw_backend.model.SignUpRequest;
import com.example.tdw_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User user = userService.login(loginRequest);
            return ResponseEntity.ok(user); // 성공적으로 로그인한 경우
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 이메일이 잘못된 경우
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
