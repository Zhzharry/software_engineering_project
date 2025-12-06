package com.example.module.repository.mysql;

import com.example.module.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    List<User> findByStatus(Integer status);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %?1%")
    List<User> findByEmailContaining(String email);
    
    boolean existsByUsername(String username);
}