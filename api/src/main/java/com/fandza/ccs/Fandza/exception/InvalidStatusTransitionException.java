package com.fandza.ccs.Fandza.exception;

import com.fandza.ccs.Fandza.enums.AccountStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(AccountStatus current, AccountStatus target) {
        super("Cannot transition account status from " + current + " to " + target);
    }
}
