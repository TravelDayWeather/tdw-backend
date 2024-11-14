package com.example.tdw_backend.repository;

import com.example.tdw_backend.entity.Token;
import com.example.tdw_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUser(User user);

    int deleteByUser(User user);

}
