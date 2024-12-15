package com.example.notemanager.controller;

import com.example.notemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {NoteController.class})
@RequiredArgsConstructor
public class UserNameController {
    private final UserService userService;
    private static final String DEFAULT_USER_NAME = "Guest";

    @ModelAttribute("username")
    public String getUsername() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            return userService.getAuthenticatedUser().getUserName();
        }
        return DEFAULT_USER_NAME;
    }

}