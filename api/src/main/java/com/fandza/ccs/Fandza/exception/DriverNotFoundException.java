package com.fandza.ccs.Fandza.exception;

import java.util.UUID;

public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(UUID id) {
        super("Driver with id " + id + " was not found");
    }
}
