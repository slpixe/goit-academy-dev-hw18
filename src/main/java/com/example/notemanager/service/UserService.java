package com.example.notemanager.service;

import com.example.notemanager.exception.EntityException;
import com.example.notemanager.exception.ExceptionMessages;
import com.example.notemanager.model.User;
import com.example.notemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       @Qualifier("passEncoder") PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        updateUser(username, user -> user.setFailedAttempts(user.getFailedAttempts() + 1));
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        updateUser(username, user -> user.setFailedAttempts(0));
    }

    @Transactional
    public void lockAccount(String username, LocalDateTime lockUntil) {
        updateUser(username, user -> {
            user.setAccountLockedUntil(lockUntil);
            user.setFailedAttempts(0);
        });
    }

    private void updateUser(String username, Consumer<User> updater) {
        User user = findByUserName(username);
        updater.accept(user);
        userRepository.save(user);
    }

}
