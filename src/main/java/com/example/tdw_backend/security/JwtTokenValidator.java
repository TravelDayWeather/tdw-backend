package com.example.tdw_backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class JwtTokenValidator {

    @Value("${app.jwtSecret}")
    private String jwtSecret;// JWT 비밀 키

    // 토큰이 유효한지 검사하는 메서드
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;  // 유효한 토큰
        } catch (ExpiredJwtException e) {
            return false;  // 만료된 토큰
        } catch (Exception e) {
            return false;  // 다른 예외 발생 (변조된 토큰 등)
        }
    }
}