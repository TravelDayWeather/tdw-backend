package com.example.tdw_backend.service;

import com.example.tdw_backend.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User signUp(User user);

}
