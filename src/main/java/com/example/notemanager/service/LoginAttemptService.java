package com.example.notemanager.service;

import com.example.notemanager.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {
    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_DURATION_MINUTES = 15;

    private final UserService userService;

    public LoginAttemptService(UserService userService) {
        this.userService = userService;
    }

    public void recordFailedAttempt(String username) {
        try {
            log.info("Recording failed login attempt for user {}", username);
            userService.incrementFailedAttempts(username);

            User user = userService.findByUserName(username);
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                log.warn("User {} exceeded max failed attempts, locking account", username);
                userService.lockAccount(username, LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            }
        } catch (Exception e) {
            log.error("Error recording failed login attempt for user {}: {}", username, e.getMessage(), e);
        }
    }

    public void resetFailedAttempts(String username) {
        try {
            log.info("Resetting failed login attempts for user {}", username);
            userService.resetFailedAttempts(username);
        } catch (Exception e) {
            log.error("Error resetting failed attempts for user {}: {}", username, e.getMessage(), e);
        }
    }

    public boolean isAccountLocked(String username) {
        try {
            log.info("Checking if user {} is locked", username);
            User user = userService.findByUserName(username);
            boolean isLocked = user.getAccountLockedUntil() != null &&
                    user.getAccountLockedUntil().isAfter(LocalDateTime.now());
            if (isLocked) {
                log.warn("User {} is locked until {}", username, user.getAccountLockedUntil());
            }
            return isLocked;
        } catch (Exception e) {
            log.error("Error checking account lock status for user {}: {}", username, e.getMessage(), e);
            return false;
        }
    }
}