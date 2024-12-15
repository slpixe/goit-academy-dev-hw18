package com.example.notemanager.controller;

import com.example.notemanager.model.Note;
import com.example.notemanager.model.dto.response.NoteResponse;
import com.example.notemanager.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteAPIController {

    private final NoteService noteService;

    @GetMapping()
    public Page<NoteResponse> listAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return noteService.listAll(pageRequest);
    }

    @GetMapping("/{id}")
    public NoteResponse getById(@PathVariable Long id) {
        Note note = noteService.getById(id);
        return NoteResponse.builder()
                .title(note.getTitle())
                .content(note.getContent())
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }

    @PutMapping("/{id}")
    public NoteResponse edit(@PathVariable Long id,
                                     @Valid @RequestBody Note note) {
        note.setId(id);
        return noteService.update(note);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody Note note) {
        return noteService.create(note);
    }

//    @GetMapping
//    public Page<NoteResponse> getNotes(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        PageRequest pageRequest = PageRequest.of(page, size);
//        if (keyword != null && !keyword.isEmpty()) {
//            return noteService.search(keyword, pageRequest);
//        } else {
//            return noteService.listAll(pageRequest);
//        }
//    }
}
