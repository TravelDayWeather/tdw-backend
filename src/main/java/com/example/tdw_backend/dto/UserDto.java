package com.example.tdw_backend.dto;

import com.example.tdw_backend.entity.User;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    public Long userId;
    public String email;
    public String pw;
    public String name;
    public String nickname;
    public String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime joinedDate;

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .userId(userDto.getUserId())
                .email(userDto.getEmail())
                .pw(userDto.getPw())
                .name(userDto.getName())
                .nickname(userDto.getNickname())
                .phone(userDto.getPhone())
                .joinedDate(userDto.getJoinedDate())
                .build();
    }
}
