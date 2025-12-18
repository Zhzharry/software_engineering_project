package com.example.module.service.impl;

import com.example.module.entity.mysql.User;
import com.example.module.repository.mysql.UserRepository;
import com.example.module.service.UserService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Result<User> createUser(User user) {
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                return Result.error("用户名已存在");
            }
            User savedUser = userRepository.save(user);
            log.info("用户创建成功: {}", savedUser.getUsername());
            return Result.success("用户创建成功", savedUser);
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage());
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<User> getUserById(Long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            return user.map(u -> Result.success(u))
                      .orElse(Result.error("用户不存在"));
        } catch (Exception e) {
            log.error("获取用户失败: {}", e.getMessage());
            return Result.error("获取用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<User> getUserByUsername(String username) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            return user.map(u -> Result.success(u))
                      .orElse(Result.error("用户不存在"));
        } catch (Exception e) {
            log.error("获取用户失败: {}", e.getMessage());
            return Result.error("获取用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage());
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Page<User>> getUsersByPage(Pageable pageable) {
        try {
            Page<User> users = userRepository.findAll(pageable);
            return Result.success(users);
        } catch (Exception e) {
            log.error("分页获取用户失败: {}", e.getMessage());
            return Result.error("分页获取用户失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<User> updateUser(Long id, User user) {
        try {
            if (!userRepository.existsById(id)) {
                return Result.error("用户不存在");
            }
            user.setId(id);
            User updatedUser = userRepository.save(user);
            log.info("用户更新成功: {}", updatedUser.getUsername());
            return Result.success("用户更新成功", updatedUser);
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage());
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return Result.error("用户不存在");
            }
            userRepository.deleteById(id);
            log.info("用户删除成功: {}", id);
            return Result.success("用户删除成功", true);
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkUsernameExists(String username) {
        try {
            boolean exists = userRepository.existsByUsername(username);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查用户名是否存在失败: {}", e.getMessage());
            return Result.error("检查用户名是否存在失败: " + e.getMessage());
        }
    }
}