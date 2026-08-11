package com.stockpilot.backend.service;

import com.stockpilot.backend.dto.LoginRequest;
import com.stockpilot.backend.dto.LoginResponse;
import com.stockpilot.backend.entity.User;
import com.stockpilot.backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
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