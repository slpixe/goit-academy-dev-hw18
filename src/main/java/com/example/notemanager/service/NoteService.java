package com.example.notemanager.service;

import com.example.notemanager.exception.ExceptionMessages;
import com.example.notemanager.exception.NoteServiceException;
import com.example.notemanager.model.Note;
import com.example.notemanager.model.User;
import com.example.notemanager.model.dto.response.NoteResponse;
import com.example.notemanager.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;

    public Page<NoteResponse> listAll(PageRequest pageRequest) {
        User currentUser = userService.getAuthenticatedUser();
        return noteRepository.findByUser(currentUser, pageRequest)
                .map(note -> NoteResponse.builder()
                        .content(note.getContent())
                        .title(note.getTitle())
                        .build());
    }

    public Note getById(long id) {
        User currentUser = userService.getAuthenticatedUser();
        return noteRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoteServiceException(ExceptionMessages.NOTE_NOT_FOUND.getMessage()));
    }

    public NoteResponse create(Note note) {
        User currentUser = userService.getAuthenticatedUser();
        if (note.getTitle() == null || note.getTitle().isEmpty()) {
            throw new NoteServiceException(ExceptionMessages.INVALID_NOTE_DATA.getMessage());
        }
        note.setUser(currentUser);
        Note savedNote = noteRepository.save(note);
        return NoteResponse.builder()
                .title(savedNote.getTitle())
                .content(savedNote.getContent())
                .build();
    }

    @Transactional
    public NoteResponse update(Note note) {
        User currentUser = userService.getAuthenticatedUser();
        Note existingNote = noteRepository.findByIdAndUser(note.getId(), currentUser)
                .orElseThrow(() -> new NoteServiceException(ExceptionMessages.NOTE_NOT_FOUND.getMessage()));
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        Note updatedNote = noteRepository.save(existingNote);
        return NoteResponse.builder()
                .title(updatedNote.getTitle())
                .content(updatedNote.getContent())
                .build();
    }

    public void delete(long id) {
        User currentUser = userService.getAuthenticatedUser();
        Note note = noteRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoteServiceException(ExceptionMessages.NOTE_NOT_FOUND.getMessage()));
        noteRepository.delete(note);
    }

    public Page<NoteResponse> search(String keyword, PageRequest pageRequest) {
        User currentUser = userService.getAuthenticatedUser();
        return noteRepository.findByUserAndKeyword(currentUser, keyword, pageRequest)
                .map(note -> NoteResponse.builder()
                        .title(note.getTitle())
                        .content(note.getContent())
                        .build());
    }
}
