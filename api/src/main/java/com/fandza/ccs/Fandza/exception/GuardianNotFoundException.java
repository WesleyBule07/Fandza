package com.fandza.ccs.Fandza.exception;

import java.util.UUID;

public class GuardianNotFoundException extends RuntimeException {

    public GuardianNotFoundException(UUID id) {
        super("Legal guardian with id " + id + " was not found");
    }
}
