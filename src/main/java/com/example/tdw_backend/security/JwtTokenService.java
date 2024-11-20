package com.example.tdw_backend.security;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.repository.TokenRepository;
import com.example.tdw_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class JwtTokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    private final Set<String> invalidatedTokens = new HashSet<>();

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    // 로그인 시 기존 AccessToken이 있으면 그대로 사용하고, 만료되면 RefreshToken을 발급
    @Transactional
    public Token loginOrRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 기존 AccessToken이 DB에 있는지 확인
        Optional<Token> existingToken = tokenRepository.findByUser(user);

        if (existingToken.isPresent()) {
            Token token = existingToken.get();

            // AccessToken이 만료되었으면 RefreshToken 발급
            if (jwtTokenProvider.isTokenExpired(token.getAccessToken())) {
                // RefreshToken 발급
                String refreshToken = jwtTokenProvider.createRefreshToken(user);
                token.setRefreshToken(refreshToken);
                token.setRefreshTokenExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getRefreshExpirationTime()));

                return tokenRepository.save(token);
            } else {
                // AccessToken이 만료되지 않았다면 refreshToken을 갱신하지 않음
                token.setRefreshToken(null);
                token.setRefreshTokenExpiryDate(null);
                return token; // 기존 토큰 그대로 반환
            }
        } else {
            Token token = new Token();
            token.setUser(user);

            String accessToken = jwtTokenProvider.createAccessToken(user);

            token.setAccessToken(accessToken);
            token.setRefreshToken(null);
            token.setAccessTokenExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getAccessExpirationTime()));
            token.setRefreshTokenExpiryDate(null);

            return tokenRepository.save(token);
        }
    }
}