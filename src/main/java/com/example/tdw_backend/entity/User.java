package com.example.tdw_backend.entity;

import com.example.tdw_backend.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @CreationTimestamp
    @Column(name = "joined_date", updatable = false)
    public LocalDateTime joinedDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens;  // 역방향 관계 설정

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