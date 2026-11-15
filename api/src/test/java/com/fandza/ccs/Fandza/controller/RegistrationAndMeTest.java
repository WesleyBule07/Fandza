package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.TestcontainersConfiguration;
import com.fandza.ccs.Fandza.repository.UserRepository;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationAndMeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanBefore() {
        userRepository.deleteAll();
    }

    @AfterEach
    void cleanAfter() {
        userRepository.deleteAll();
    }

    private String registerPayload(String contactValue) {
        return """
                {
                  "profileType": "LEGAL_GUARDIAN",
                  "fullName": "Maria Sitoe",
                  "contactType": "PHONE",
                  "contactValue": "%s"
                }
                """.formatted(contactValue);
    }

    @Test
    void registerShouldNormalizePhoneAndCreatePendingAccount() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(registerPayload("840000000")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactValue").value("+258840000000"))
                .andExpect(jsonPath("$.contactType").value("PHONE"))
                .andExpect(jsonPath("$.contactVerified").value(false))
                .andExpect(jsonPath("$.accountStatus").value("PENDING"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void registerShouldReturn409WhenContactAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(registerPayload("841111111")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(registerPayload("+258841111111")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registerShouldReturn400WhenPhoneIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(registerPayload("12345")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void meShouldAutoProvisionFromKeycloakIdentityAndStayStable() throws Exception {
        String subject = "8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e";
        String first = mockMvc.perform(get("/api/v1/me")
                        .with(jwt().jwt(token -> token
                                .subject(subject)
                                .claim("email", "jane@fandza.co.mz")
                                .claim("name", "Jane Doe"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(subject))
                .andExpect(jsonPath("$.contactType").value("EMAIL"))
                .andExpect(jsonPath("$.contactVerified").value(false))
                .andExpect(jsonPath("$.profileType").value("LEGAL_GUARDIAN"))
                .andReturn().getResponse().getContentAsString();

        String second = mockMvc.perform(get("/api/v1/me")
                        .with(jwt().jwt(token -> token
                                .subject(subject)
                                .claim("email", "jane@fandza.co.mz")
                                .claim("name", "Jane Doe"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(first).get("id").asText())
                .isEqualTo(objectMapper.readTree(second).get("id").asText());
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void meShouldLinkKeycloakToPreRegisteredAccount() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "profileType": "DRIVER",
                                  "fullName": "Jane Doe",
                                  "contactType": "EMAIL",
                                  "contactValue": "jane@fandza.co.mz"
                                }
                                """))
                .andExpect(status().isCreated());

        String subject = "8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e";
        mockMvc.perform(get("/api/v1/me")
                        .with(jwt().jwt(token -> token
                                .subject(subject)
                                .claim("email", "jane@fandza.co.mz")
                                .claim("name", "Jane Doe")
                                .claim("realm_access", java.util.Map.of("roles",
                                        java.util.List.of("fandza-driver"))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(subject))
                .andExpect(jsonPath("$.profileType").value("DRIVER"));

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void patchMeShouldUpdateFullName() throws Exception {
        String subject = "8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e";
        var meJwt = jwt().jwt(token -> token
                .subject(subject)
                .claim("email", "jane@fandza.co.mz")
                .claim("name", "Jane Doe"));

        mockMvc.perform(patch("/api/v1/me")
                        .with(meJwt)
                        .contentType(APPLICATION_JSON)
                        .content("{\"fullName\":\"Jane Macuácua\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Macuácua"));

        mockMvc.perform(get("/api/v1/me").with(meJwt))
                .andExpect(jsonPath("$.fullName").value("Jane Macuácua"));
    }

    @Test
    void profileTypeShouldBeChangeableWhilePendingAndLockedAfterwards() throws Exception {
        String subject = "8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e";
        var meJwt = jwt().jwt(token -> token
                .subject(subject)
                .claim("email", "jane@fandza.co.mz")
                .claim("name", "Jane Doe"));

        mockMvc.perform(put("/api/v1/me/profile-type")
                        .with(meJwt)
                        .contentType(APPLICATION_JSON)
                        .content("{\"profileType\":\"DRIVER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileType").value("DRIVER"));

        mockMvc.perform(put("/api/v1/me/profile-type")
                        .with(meJwt)
                        .contentType(APPLICATION_JSON)
                        .content("{\"profileType\":\"LEGAL_GUARDIAN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileType").value("LEGAL_GUARDIAN"));
    }

    @Test
    void profileTypeShouldBeLockedOnceVerified() throws Exception {
        String subject = "8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e";
        var meJwt = jwt().jwt(token -> token
                .subject(subject)
                .claim("email", "jane@fandza.co.mz")
                .claim("name", "Jane Doe"));

        mockMvc.perform(get("/api/v1/me").with(meJwt))
                .andExpect(status().isOk());

        var account = userRepository.findByKeycloakId(java.util.UUID.fromString(subject)).orElseThrow();
        account.setContactVerified(true);
        userRepository.save(account);

        mockMvc.perform(put("/api/v1/me/profile-type")
                        .with(meJwt)
                        .contentType(APPLICATION_JSON)
                        .content("{\"profileType\":\"DRIVER\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void meShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/me"))
                .andExpect(status().isUnauthorized());
    }
}
