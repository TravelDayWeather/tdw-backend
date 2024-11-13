package com.example.tdw_backend.payload;

import com.example.tdw_backend.entity.User;

public class LoginResponse {
    private User user;
    private String jwtToken;
    private String refreshToken;

    public LoginResponse(User user, String jwtToken, String refreshToken) {
        this.user = user;
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
    }
}