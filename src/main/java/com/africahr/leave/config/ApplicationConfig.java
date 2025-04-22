package com.africahr.leave.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.africahr.leave.repository")
@EnableTransactionManagement
public class ApplicationConfig {
} 