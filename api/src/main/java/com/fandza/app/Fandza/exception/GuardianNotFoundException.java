package com.fandza.app.Fandza.exception;

public class GuardianNotFoundException extends RuntimeException {

    public GuardianNotFoundException(Long id) {
        super("Legal guardian with id " + id + " was not found");
    }
}
