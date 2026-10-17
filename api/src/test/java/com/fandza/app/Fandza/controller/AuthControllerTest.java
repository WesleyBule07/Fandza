package com.fandza.app.Fandza.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import com.fandza.app.Fandza.dtos.auth.LoginRequest;
import com.fandza.app.Fandza.entity.Driver;
import com.fandza.app.Fandza.repository.AppUserRepository;
import com.fandza.app.Fandza.repository.DriverRepository;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    void cleanDatabase() {
        cleanTables();
    }

    @AfterEach
    void cleanDatabaseAfterTest() {
        cleanTables();
    }

    private void cleanTables() {
        driverRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    private void createDriver(String email, String rawPassword) {
        Driver driver = new Driver("Jane Doe", email, passwordEncoder.encode(rawPassword));
        driver.setPhoneNumber("+258840000000");
        driver.setLicenseNumber("L-123");
        driver.setLicenseExpiry(OffsetDateTime.now(BUSINESS_ZONE).plusYears(1));
        driverRepository.save(driver);
    }

    private String loginAndGetToken(String email, String rawPassword) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, rawPassword))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    @Test
    void loginShouldReturnBearerTokenWithoutExposingPassword() throws Exception {
        createDriver("jane@fandza.co.mz", "secret123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("jane@fandza.co.mz", "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(notNullValue()))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("jane@fandza.co.mz"))
                .andExpect(jsonPath("$.userType").value("DRIVER"))
                .andExpect(jsonPath("$.expiresAt").value(notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void loginShouldReturn401ForWrongPassword() throws Exception {
        createDriver("jane@fandza.co.mz", "secret123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("jane@fandza.co.mz", "wrong-pass"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void loginShouldReturn401ForUnknownEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("ghost@fandza.co.mz", "secret123"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void meShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/me"));
    }

    @Test
    void meShouldReturnAuthenticatedDriver() throws Exception {
        createDriver("jane@fandza.co.mz", "secret123");
        String token = loginAndGetToken("jane@fandza.co.mz", "secret123");

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@fandza.co.mz"))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.userType").value("DRIVER"));
    }

    @Test
    void protectedEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isUnauthorized());

        createDriver("jane@fandza.co.mz", "secret123");
        String token = loginAndGetToken("jane@fandza.co.mz", "secret123");

        mockMvc.perform(get("/api/v1/drivers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
