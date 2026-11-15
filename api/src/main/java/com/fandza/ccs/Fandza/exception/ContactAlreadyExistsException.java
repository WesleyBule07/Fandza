package com.fandza.ccs.Fandza.exception;

public class ContactAlreadyExistsException extends RuntimeException {

    public ContactAlreadyExistsException(String contactValue) {
        super("A user with this contact already exists");
    }
}
