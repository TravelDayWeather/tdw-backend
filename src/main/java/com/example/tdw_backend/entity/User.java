package com.example.tdw_backend.entity;

import com.example.tdw_backend.dto.UserDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email")
    private String email;

    @Column(name = "pw")
    private String pw;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "phone")
    private String phone;

    @Column(name = "joined_date")
    private Timestamp joinedDate;

    public static UserDto toDTO(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .pw(user.getPw())
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .joinedDate(String.valueOf(user.getJoinedDate()))
                .build();
    }
}
