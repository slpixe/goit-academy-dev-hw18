package com.example.notemanager.model.dto;

import com.example.notemanager.model.Note;
import com.example.notemanager.model.dto.response.NoteResponse;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {
    public NoteResponse toResponse(Note note) {
        return NoteResponse.builder()
                .title(note.getTitle())
                .content(note.getContent())
                .build();
    }
}