package com.example.notemanager;

import com.example.notemanager.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Autowired
    private UserContext userContext;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Grab the authentication from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
                // You can grab the actual User entity from your CustomUserDetails
                User user = customUserDetails.getUser();
                System.out.println("Add user to cache = " + user.getUsername());
                userContext.setCachedUser(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}

