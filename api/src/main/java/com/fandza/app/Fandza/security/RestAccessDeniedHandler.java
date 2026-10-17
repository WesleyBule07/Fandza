package com.fandza.app.Fandza.security;

import tools.jackson.databind.ObjectMapper;
import com.fandza.app.Fandza.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(BUSINESS_ZONE),
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "Access denied",
                request.getRequestURI(),
                null);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
