package com.example.module.service;

import com.example.module.dto.LoginRequest;
import com.example.module.dto.LoginResponse;
import com.example.module.util.Result;

public interface AuthService {
    Result<LoginResponse> login(LoginRequest request);
    
    Result<Boolean> logout(String token);
    
    Result<com.example.module.entity.mysql.User> getCurrentUser(String token);
}

