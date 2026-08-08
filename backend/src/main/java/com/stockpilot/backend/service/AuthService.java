package com.stockpilot.backend.service;

import com.stockpilot.backend.dto.LoginRequest;
import com.stockpilot.backend.dto.LoginResponse;
import com.stockpilot.backend.entity.User;
import com.stockpilot.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {

            return new LoginResponse(
                    false,
                    null,
                    null,
                    "Invalid username or password"
            );
        }

        if (!user.getPassword().equals(request.getPassword())) {

            return new LoginResponse(
                    false,
                    null,
                    null,
                    "Invalid username or password"
            );
        }

        return new LoginResponse(
                true,
                user.getUsername(),
                user.getRole(),
                "Login successful"
        );
    }
}