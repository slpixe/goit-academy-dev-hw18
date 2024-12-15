package com.example.notemanager.service;

import com.example.notemanager.controller.AuthController;
import com.example.notemanager.exception.EntityException;
import com.example.notemanager.exception.ExceptionMessages;
import com.example.notemanager.model.User;
import com.example.notemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new EntityException(ExceptionMessages.ENTITY_NOT_FOUND.getMessage()));
    }

    public String createUser(String username, String password) {
        if (userRepository.existsByUserName(username)) {
            return "User already exists";
        }

        User user = User.builder()
                .userName(username)
                .password(passwordEncoder.encode(password))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);
        return "User created";
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityException(ExceptionMessages.ENTITY_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void incrementFailedAttempts(String username) {
        User user = findByUserName(username);
        if (user != null) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            userRepository.save(user);
        }
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        User user = findByUserName(username);
        if (user != null) {
            user.setFailedAttempts(0);
            userRepository.save(user);
        }
    }

    @Transactional
    public void lockAccount(String username, LocalDateTime lockUntil) {
        User user = findByUserName(username);
        if (user != null) {
            user.setAccountLockedUntil(lockUntil);
            user.setFailedAttempts(0);
            userRepository.save(user);
        }
    }

}
