package com.example.tdw_backend.security;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.repository.TokenRepository;
import com.example.tdw_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtTokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    // 로그인 시 기존 AccessToken이 있으면 그대로 사용하고, 만료되면 RefreshToken을 발급
    public Token loginOrRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getUserId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 기존 AccessToken이 DB에 있는지 확인
        Optional<Token> existingToken = tokenRepository.findByUser(user);

        if (existingToken.isPresent()) {
            Token token = existingToken.get();

            // AccessToken이 만료되었으면 RefreshToken 발급
            if (jwtTokenProvider.isTokenExpired(token.getAccessToken())) {
                // RefreshToken 발급
                String refreshToken = jwtTokenProvider.createRefreshToken(user);
                token.setRefreshToken(refreshToken);
                // RefreshToken 유효기간 갱신
                token.setRefreshTokenExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getRefreshExpirationTime()));
                return tokenRepository.save(token);  // DB에 RefreshToken 업데이트
            } else {
                // AccessToken이 유효하다면 그대로 사용
                return token;  // 기존 토큰 그대로 반환
            }
        } else {
            // 기존 토큰이 없으면 새로 발급
            Token token = new Token();
            token.setUser(user);
            String accessToken = jwtTokenProvider.createAccessToken(user);
            token.setAccessToken(accessToken);
            String refreshToken = jwtTokenProvider.createRefreshToken(user);
            token.setRefreshToken(refreshToken);
            token.setAccessTokenExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getAccessExpirationTime()));
            token.setRefreshTokenExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getRefreshExpirationTime()));
            return tokenRepository.save(token);  // 새 토큰 DB에 저장
        }
    }
}