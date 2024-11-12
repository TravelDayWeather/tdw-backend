package com.example.tdw_backend.entity;

import com.example.tdw_backend.dto.UserDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public Long userId;

    @Column(name = "email")
    public String email;

    @Column(name = "pw")
    public String pw;

    @Column(name = "name")
    public String name;

    @Column(name = "nickname")
    public String nickname;

    @Column(name = "phone")
    public String phone;

    @CreationTimestamp  // 가입 시 자동으로 현재 시간이 저장됨
    @Column(name = "joined_date", updatable = false)
    public LocalDateTime joinedDate;

    @Builder
    public User(Long userId, String email, String pw, String name, String nickname, String phone, LocalDateTime joinedDate) {
        this.userId = userId;
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.joinedDate = joinedDate;
    }

    public static UserDto toDTO(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .pw(user.getPw())
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .joinedDate(user.getJoinedDate())
                .build();
    }
}
