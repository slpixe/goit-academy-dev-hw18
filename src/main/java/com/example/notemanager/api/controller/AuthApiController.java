package com.example.notemanager.api.controller;

import com.example.notemanager.api.model.dto.request.UserCreateRequest;
import com.example.notemanager.api.model.dto.request.UserLoginRequest;
import com.example.notemanager.service.LoginAttemptService;
import com.example.notemanager.service.UserService;
import com.example.notemanager.api.util.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class AuthApiController {
    private static final Logger log = LoggerFactory.getLogger(AuthApiController.class);

    private final UserService userService;
    private final DaoAuthenticationProvider authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;

    public AuthApiController(UserService userService,
                             DaoAuthenticationProvider authenticationManager,
                             @Qualifier("userDetails") UserDetailsService userDetailsService,
                             JwtUtil jwtUtil,
                             LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@RequestBody UserCreateRequest request) {
        return userService.createUser(request.userName(), request.password());
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginRequest request) {
        log.info("Login request for user: {}", request.userName());

        // Step 1: Check if account is locked
        if (loginAttemptService.isAccountLocked(request.userName())) {
            log.warn("Account is locked for user: {}", request.userName());
            throw new ResponseStatusException(HttpStatus.LOCKED, "Account is locked. Try again later.");
        }

        try {
            // Step 2: Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.userName(), request.password())
            );
        } catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for user: {}", request.userName());
            // Step 3: Record failed login attempt
            loginAttemptService.recordFailedAttempt(request.userName());

            // Check if account is now locked
            if (loginAttemptService.isAccountLocked(request.userName())) {
                log.warn("Account locked for user: {} after failed attempts", request.userName());
                throw new ResponseStatusException(HttpStatus.LOCKED, "Account is locked. Try again later.");
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Step 4: Reset failed attempts on successful login
        loginAttemptService.resetFailedAttempts(request.userName());

        // Step 5: Generate JWT Token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.userName());
        log.info("Authentication successful for user: {}", request.userName());
        return jwtUtil.generateToken(userDetails);
    }

}