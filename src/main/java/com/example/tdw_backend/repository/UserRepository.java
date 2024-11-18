package com.example.tdw_backend.repository;

import com.example.tdw_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 로그인
     * @param email 회원 이메일
     * @return User
     */
    Optional<User> findByEmail(String email);

    /**
     * 유효성 검사 - 이메일 중복 체크
     * @param email 회원 이메일
     * @return true or false
     */
    boolean existsByEmail(String email);


    /**
     * 유효성 검사 - 닉네임 중복 체크
     * @param nickname 회원 닉네임
     * @return true or false
     */
    boolean existsByNickname(String nickname);

    /**
     * 마이페이지
     * @param userId 회원 아이디
     * @return User
     */
    User findByUserId(Long userId);

}
