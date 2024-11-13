package com.example.tdw_backend.security;

import com.example.tdw_backend.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserPrincipal implements UserDetails {

    @Getter
    private Long userId;

    @Getter
    @JsonIgnore
    private String email;

    @Getter
    @JsonIgnore
    private String pw;

    @Getter
    private String name;

    @Getter
    private String nickname;

    @Getter
    private String phone;

    @Getter
    private LocalDateTime joinedDate;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userId, String email, String pw, String name, String nickname, String phone,
                         LocalDateTime joinedDate, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.joinedDate = joinedDate;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getPw(),
                user.getName(),
                user.getNickname(),
                user.getPhone(),
                user.getJoinedDate(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return pw;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

