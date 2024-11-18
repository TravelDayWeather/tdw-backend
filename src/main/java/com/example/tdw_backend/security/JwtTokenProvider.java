package com.example.tdw_backend.security;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Base64;
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

    @Autowired
    private UserRepository userRepository;

    // AccessToken 생성
    public String createAccessToken(User user) {
        var signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));

        try {
            return Jwts.builder()
                    .setSubject(user.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpirationInMs))
                    .signWith(signingKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating tokens1", e);
        }
        return null;
    }

    // RefreshToken 생성
    public String createRefreshToken(User user) {
        var signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));

        try {
            return Jwts.builder()
                    .setSubject(user.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
                    .signWith(signingKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating tokens2", e);
        }
        return null;
    }

    // Token 만료 확인
    public boolean isTokenExpired(String token) {
        var signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));

        if (token == null) {
            System.out.println("Token is null");
            return false; // 기본적으로 false 반환
        }
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            System.out.println("Token expiration time: " + expiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            System.out.println("Error parsing token: " + e.getMessage());
            return true; // 예외 발생 시 만료된 것으로 간주
        }
    }

    public Long getRefreshExpirationTime() {
        return jwtRefreshExpirationInMs;
    }

    public Long getAccessExpirationTime() {
        return jwtAccessExpirationInMs;
    }

    // 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        String email = getUserEmailFromJWT(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        return new UsernamePasswordAuthenticationToken(userPrincipal, token, userPrincipal.getAuthorities());
    }

    // JWT에서 이메일을 추출하는 메서드
    private String getUserEmailFromJWT(String token) {

        var signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));  // jwtSecret을 사용해 서명 키 생성

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // JWT에서 Claims를 가져오는 메서드
    public Claims getClaimsFromToken(String token) {
        var signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));  // jwtSecret을 사용해 서명 키 생성

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String getTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}