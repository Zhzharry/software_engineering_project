package com.example.module.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.module.repository.mysql")
@EnableTransactionManagement
public class JpaConfig {
    // JPA配置可以通过application.properties/yaml文件进行配置
}