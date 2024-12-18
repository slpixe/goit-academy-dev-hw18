package com.example.notemanager.mvc.exception;

public class NoteServiceException extends RuntimeException {
    public NoteServiceException(String message) {
        super(message);
    }
}
