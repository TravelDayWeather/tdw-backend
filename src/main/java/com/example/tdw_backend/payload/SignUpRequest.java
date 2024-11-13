package com.example.tdw_backend.payload;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SignUpRequest {

    private String email;

    private String pw;

    private String name;

    private String nickname;

    private String phone;

    private LocalDateTime joined_date;
}
