package com.example.tdw_backend.security;

import com.example.tdw_backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
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
        var signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);  // 512 비트 키 생성

        try {
            String accessToken = Jwts.builder()
                    .setSubject(user.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpirationInMs))
                    .signWith(signingKey, SignatureAlgorithm.HS512)
                    .compact();
            return accessToken;
        } catch (Exception e) {
            log.error("Error generating tokens1", e);
        }
        return null;
    }

    // RefreshToken 생성
    public String createRefreshToken(User user) {
        var signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);  // 512 비트 키 생성

        try {
            String refreshToken = Jwts.builder()
                    .setSubject(user.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
                    .signWith(signingKey, SignatureAlgorithm.HS512)
                    .compact();
            return refreshToken;
        } catch (Exception e) {
            log.error("Error generating tokens2", e);
        }
        return null;
    }

    // Token 만료 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()  // 새로운 파서 빌더 사용
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))  // 서명 키 설정
                    .build()
                    .parseClaimsJws(token)
                    .getBody();  // JWT Claims 얻기

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
        Claims claims = Jwts.parserBuilder()  // 새로운 파서 빌더 사용
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))  // 서명 키 설정
                .build()
                .parseClaimsJws(token)
                .getBody();  // JWT Claims 얻기

        // 예를 들어, 사용자 ID는 'sub' 클레임에 저장된다고 가정하고 가져옵니다.
        return Long.parseLong(claims.getSubject());  // 'subject'는 일반적으로 사용자 ID나 이메일 등을 저장
    }
}