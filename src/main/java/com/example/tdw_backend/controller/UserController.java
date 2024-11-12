package com.example.tdw_backend.controller;

import com.example.tdw_backend.dto.UserDto;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.model.LoginRequest;
import com.example.tdw_backend.model.SignUpRequest;
import com.example.tdw_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/api/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest);
        return ResponseEntity.ok(user);
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
