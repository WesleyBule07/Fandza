package com.fandza.app.Fandza.security;

import tools.jackson.databind.ObjectMapper;
import com.fandza.app.Fandza.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(BUSINESS_ZONE),
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                "Authentication required",
                request.getRequestURI(),
                null);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
