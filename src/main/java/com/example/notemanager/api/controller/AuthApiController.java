package com.example.notemanager.api.controller;

import com.example.notemanager.model.dto.request.UserCreateRequest;
import com.example.notemanager.model.dto.request.UserLoginRequest;
import com.example.notemanager.service.UserService;
import com.example.notemanager.api.util.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthApiController {
    private static final Logger log = LoggerFactory.getLogger(AuthApiController.class);

    private final UserService userService;
    private final DaoAuthenticationProvider authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthApiController(UserService userService,
                             DaoAuthenticationProvider authenticationManager,
                             @Qualifier("apiUserDetails") UserDetailsService userDetailsService,
                             JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginRequest request) {
        log.info("Login request for user: {}", request.userName());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userName(), request.password())
        );
        log.info("Authentication successful for user: {}", request.userName());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.userName());
        return jwtUtil.generateToken(userDetails);
    }
}