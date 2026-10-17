package com.fandza.app.Fandza.exception;

public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(Long id) {
        super("Driver with id " + id + " was not found");
    }
}
