package com.example.tdw_backend.service;

import com.example.tdw_backend.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthorizationService {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthorizationService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    public String extractToken(String authorizationHeader) {
        try {
            return JwtTokenProvider.getTokenFromAuthorizationHeader(authorizationHeader);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
        }
    }

    /**
     * 토큰 유효성 검증
     */
    public void validateToken(String token) {
        if (jwtTokenProvider.isTokenExpired(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired");
        }
    }

    /**
     * 토큰에서 클레임 추출
     */
    public Claims getClaims(String token) {
        try {
            return jwtTokenProvider.getClaimsFromToken(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }

    /**
     * 토큰에서 사용자 이메일 추출
     */
    public String getUserEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }
}