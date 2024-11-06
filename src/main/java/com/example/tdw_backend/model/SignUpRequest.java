package com.example.tdw_backend.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class SignUpRequest {

    private BigInteger user_id;

    private String email;

    private String pw;

    private String name;

    private String nickname;

    private String phone;

    private String joined_date;
}
