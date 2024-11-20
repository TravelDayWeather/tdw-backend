package com.example.tdw_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        System.out.println("Authorization Header: " + jwt);

        if (jwt != null && !tokenProvider.isTokenExpired(jwt)) { // 토큰이 유효한 경우
            System.out.println("Token is valid and not expired.");
            Authentication authentication = tokenProvider.getAuthentication(jwt);

            System.out.println("Authentication Object: " + authentication);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Authentication object is null.");
            }
        } else {
            System.out.println("Token is invalid or expired.");
        }
        System.out.println("SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}