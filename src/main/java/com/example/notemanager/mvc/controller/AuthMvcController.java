package com.example.notemanager.mvc.controller;

import com.example.notemanager.model.User;
import com.example.notemanager.mvc.security.LoginAttemptService;
import com.example.notemanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthMvcController {
    private static final Logger log = LoggerFactory.getLogger(AuthMvcController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public AuthMvcController(UserService userService,
                             @Qualifier("passEncoder") PasswordEncoder passwordEncoder,
                             LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletRequest request) {
        try {
            log.info("Attempting to authenticate user {}", username);

            if (loginAttemptService.isAccountLocked(username)) {
                log.warn("User {} is locked out", username);
                return "redirect:/login?error=LockedOut";
            }

            User user = userService.findByUserName(username);
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("Invalid credentials for user {}", username);
                loginAttemptService.recordFailedAttempt(username);
                return "redirect:/login?error=InvalidCredentials";
            }

            loginAttemptService.resetFailedAttempts(username);
            authenticateUser(user, request);
            log.info("User {} authenticated successfully", username);

            return "redirect:/note/list";
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            return "redirect:/login?error=UnexpectedError";
        }
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam("username") String username,
                         @RequestParam("password") String password) {
        try {
            userService.createUser(username, password);
        } catch (Exception e) {
            log.error("Error creating the user {}", e.getMessage());
        }
        log.info("User {} created", username);
        return "redirect:/login";
    }

    private void authenticateUser(User user, HttpServletRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserName(),
                null,
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUserName())
                        .password(user.getPassword())
                        .authorities(user.getRole())
                        .build()
                        .getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
}
