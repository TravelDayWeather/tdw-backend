package com.example.tdw_backend.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "token")
public class Token implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "accessToken")
    private String accessToken;

    @Column(name = "accessTokenExpiryDate")
    private Instant accessTokenExpiryDate;

    @Nullable
    @Column(name = "refreshToken")
    private String refreshToken;

    @Nullable
    @Column(name = "refreshTokenExpiryDate")
    private Instant refreshTokenExpiryDate;
}