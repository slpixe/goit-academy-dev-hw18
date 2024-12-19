package com.example.notemanager.api.model.dto.request;

import lombok.Builder;

@Builder
public record UserCreateRequest(String userName, String password) {}