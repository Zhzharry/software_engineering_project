package com.example.module.controller;

import com.example.module.dto.LoginRequest;
import com.example.module.dto.LoginResponse;
import com.example.module.service.AuthService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        return authService.logout(token);
    }
    
    @GetMapping("/current-user")
    public Result<com.example.module.entity.mysql.User> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String token) {
        return authService.getCurrentUser(token);
    }
}

