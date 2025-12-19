package com.example.module.service.impl;

import com.example.module.dto.LoginRequest;
import com.example.module.dto.LoginResponse;
import com.example.module.entity.mysql.User;
import com.example.module.repository.mysql.UserRepository;
import com.example.module.service.AuthService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    
    // 简单的token存储（生产环境应使用Redis等）
    // private static final Map<String, User> tokenStore = new ConcurrentHashMap<>();
    
    @Override
    public Result<LoginResponse> login(LoginRequest request) {
        try {
            // 验证用户名和密码
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            
            if (!userOpt.isPresent()) {
                return Result.error("用户名或密码错误");
            }
            
            User user = userOpt.get();
            
            // 检查用户状态
            if (user.getStatus() != null && user.getStatus() == 0) {
                return Result.error("用户已被禁用");
            }
            
            // 验证手机号
            if (user.getPhone() == null || user.getPhone().isEmpty()) {
                return Result.error("该用户未绑定手机号，请联系管理员");
            }
            
            // 验证手机号是否匹配
            if (!user.getPhone().equals(request.getPhone())) {
                return Result.error("用户名或手机号错误");
            }
            
            // 生成token（简单实现，生产环境应使用JWT）
            String token = UUID.randomUUID().toString().replace("-", "");
            
            // 创建响应（不返回密码）
            User userResponse = new User();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setEmail(user.getEmail());
            userResponse.setPhone(user.getPhone());
            userResponse.setStatus(user.getStatus());
            userResponse.setCreateTime(user.getCreateTime());
            userResponse.setUpdateTime(user.getUpdateTime());
            
            LoginResponse loginResponse = new LoginResponse(token, userResponse);
            
            log.info("用户登录成功: {}", user.getUsername());
            return Result.success("登录成功", loginResponse);
            
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> logout(String token) {
        try {
            // 简单的token清除（生产环境应从Redis等清除）
            log.info("用户退出登录");
            return Result.success("退出登录成功", true);
        } catch (Exception e) {
            log.error("退出登录失败: {}", e.getMessage());
            return Result.error("退出登录失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<User> getCurrentUser(String token) {
        try {
            // 简单的token验证（生产环境应从Redis等获取用户信息）
            // 这里简化处理，直接返回错误，让前端从localStorage获取
            return Result.error("请重新登录");
        } catch (Exception e) {
            log.error("获取当前用户失败: {}", e.getMessage());
            return Result.error("获取当前用户失败: " + e.getMessage());
        }
    }
}

