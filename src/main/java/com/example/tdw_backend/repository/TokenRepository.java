package com.example.tdw_backend.repository;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * 토큰으로 유저 찾기
     * @param  user 회원
     * @return token 토큰
     */
    Optional<Token> findByUser(User user);

    /**
     * 유저 탈퇴
     * @param user 회원
     * @return 0 또는 1
     */
    int deleteByUser(User user);

    User findByRefreshToken(String token);
}
