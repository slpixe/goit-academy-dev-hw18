package com.example.notemanager;

import com.example.notemanager.model.User;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final User user;

    public CustomUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
