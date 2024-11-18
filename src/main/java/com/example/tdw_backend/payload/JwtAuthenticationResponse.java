package com.example.tdw_backend.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {

    private Long userId;
    @Setter
    private String accessToken;
    @Setter
    private String refreshToken;

    public JwtAuthenticationResponse(Long userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
