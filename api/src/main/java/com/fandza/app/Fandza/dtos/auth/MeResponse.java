package com.fandza.app.Fandza.dtos.auth;

import com.fandza.app.Fandza.enums.UserType;

public record MeResponse(
        String email,
        String fullName,
        UserType userType
) {
}
