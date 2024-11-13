package com.example.tdw_backend.security;

import com.example.tdw_backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtAccessExpirationInMs}")
    private Long jwtAccessExpirationInMs;

    @Value("${app.jwtRefreshExpirationInMs}")
    private Long jwtRefreshExpirationInMs;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // AccessToken 생성
    public String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Token 만료 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Long getRefreshExpirationTime() {
        return jwtRefreshExpirationInMs;
    }

    public Long getAccessExpirationTime() {
        return jwtAccessExpirationInMs;
    }

    // JWT에서 사용자 ID 추출하는 메서드 추가
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)  // 서명 키로 비밀 키를 사용
                .parseClaimsJws(token)    // 토큰 파싱
                .getBody();  // Claims 객체 반환

        // 예를 들어, 사용자 ID는 'sub' 클레임에 저장된다고 가정하고 가져옵니다.
        return Long.parseLong(claims.getSubject());  // 'subject'는 일반적으로 사용자 ID나 이메일 등을 저장
    }

    // JWT 유효성 검증 메서드 예시
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}