package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void meShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/me"));
    }

    @Test
    void meShouldReturnIdentityClaimsFromToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .with(jwt().jwt(token -> token
                                .subject("8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e")
                                .claim("preferred_username", "jane")
                                .claim("email", "jane@fandza.co.mz")
                                .claim("name", "Jane Doe"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value("8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e"))
                .andExpect(jsonPath("$.username").value("jane"))
                .andExpect(jsonPath("$.email").value("jane@fandza.co.mz"))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    void protectedEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/drivers")
                        .with(jwt()))
                .andExpect(status().isOk());
    }
}
