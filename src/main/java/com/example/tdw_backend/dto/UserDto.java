package com.example.tdw_backend.dto;

import com.example.tdw_backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@ToString
public class UserDto {

    private Long userId;
    private String email;
    private String pw;
    private String name;
    private String nickname;
    private String phone;
    private String joinedDate;

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .userId(userDto.getUserId())
                .email(userDto.getEmail())
                .pw(userDto.getPw())
                .name(userDto.getName())
                .nickname(userDto.getNickname())
                .phone(userDto.getPhone())
                .joinedDate(Timestamp.valueOf(userDto.getJoinedDate()))
                .build();
    }
}
