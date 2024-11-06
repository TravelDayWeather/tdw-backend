package com.example.tdw_backend.controller;

import com.example.tdw_backend.dto.UserDto;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.model.LoginRequest;
import com.example.tdw_backend.model.SignUpRequest;
import com.example.tdw_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserService userService;

    // 회원가입
    @PostMapping("api/signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpRequest signUpRequest) {
        UserDto userDto = UserDto.builder()
                .email(signUpRequest.getEmail())
                .pw(signUpRequest.getPw())  // 비밀번호 필드도 요청에서 가져오기
                .name(signUpRequest.getName())
                .nickname(signUpRequest.getNickname())
                .phone(signUpRequest.getPhone())
                .joinedDate(java.time.LocalDateTime.now().toString())  // 현재 시간으로 설정
                .build();

        User user = UserDto.toEntity(userDto);

        userService.signUp(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    // 로그인
    @GetMapping("api/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest);

        return ResponseEntity.ok(user);
    }
}
