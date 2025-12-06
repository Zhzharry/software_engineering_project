package com.example.module.service;

import com.example.module.entity.mysql.User;
import com.example.module.util.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Result<User> createUser(User user);
    
    Result<User> getUserById(Long id);
    
    Result<User> getUserByUsername(String username);
    
    Result<List<User>> getAllUsers();
    
    Result<Page<User>> getUsersByPage(Pageable pageable);
    
    Result<User> updateUser(Long id, User user);
    
    Result<Boolean> deleteUser(Long id);
    
    Result<Boolean> checkUsernameExists(String username);
}