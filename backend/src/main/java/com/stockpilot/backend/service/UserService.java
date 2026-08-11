package com.stockpilot.backend.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stockpilot.backend.dto.UserResponse;
import com.stockpilot.backend.entity.User;
import com.stockpilot.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return toResponse(user);

    }

    public UserResponse createUser(User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);

    }

    public UserResponse updateUser(
            Long id,
            User updatedUser
    ) {

        User existing = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        existing.setUsername(updatedUser.getUsername());
        existing.setRole(updatedUser.getRole());

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {

            existing.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }

        User savedUser = userRepository.save(existing);

        return toResponse(savedUser);

    }

    public void deleteUser(Long id) {

        userRepository.deleteById(id);

    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

    }
}