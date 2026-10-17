package com.fandza.app.Fandza.enums;

public enum UserType {
    DRIVER,
    LEGAL_GUARDIAN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
