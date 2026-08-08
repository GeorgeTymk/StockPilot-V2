package com.stockpilot.backend.config;

import com.stockpilot.backend.entity.User;
import com.stockpilot.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "ADMIN"
                );

                userRepository.save(admin);

                System.out.println("=================================");
                System.out.println("StockPilot admin user created");
                System.out.println("Username: admin");
                System.out.println("=================================");
            }
        };
    }
}