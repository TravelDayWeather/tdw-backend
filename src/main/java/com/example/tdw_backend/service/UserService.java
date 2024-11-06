package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.User;
import com.example.tdw_backend.model.LoginRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User signUp(User user);

    User login(LoginRequest loginRequest);

}
