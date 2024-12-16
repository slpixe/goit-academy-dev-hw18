package com.example.notemanager.model.dto.request;

import lombok.Builder;

@Builder
public record NoteCreateRequest(String title, String content) {
}