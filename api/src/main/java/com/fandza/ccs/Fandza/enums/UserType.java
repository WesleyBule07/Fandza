package com.fandza.ccs.Fandza.enums;

public enum UserType {
    DRIVER,
    LEGAL_GUARDIAN,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
