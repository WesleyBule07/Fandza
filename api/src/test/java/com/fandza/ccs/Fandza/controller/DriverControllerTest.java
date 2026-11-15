package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.fandza.ccs.Fandza.dtos.auth.LoginRequest;
import com.fandza.ccs.Fandza.dtos.driver.DriverRequest;
import com.fandza.ccs.Fandza.entity.Admin;
import com.fandza.ccs.Fandza.entity.Driver;
import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.repository.AdminRepository;
import com.fandza.ccs.Fandza.repository.AppUserRepository;
import com.fandza.ccs.Fandza.repository.DriverRepository;
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

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DriverControllerTest {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");
    private static final String MISSING_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        cleanTables();
    }

    @AfterEach
    void cleanDatabaseAfterTest() {
        cleanTables();
    }

    private void cleanTables() {
        adminRepository.deleteAll();
        driverRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    private DriverRequest validRequest(String email) {
        return new DriverRequest(
                "Jane Doe",
                email,
                "secret123",
                "+258840000000",
                "L-123",
                OffsetDateTime.now(BUSINESS_ZONE).plusYears(1));
    }

    private String createDriverAndGetId(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(email))))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asText();
    }

    private String loginAndGetToken(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "secret123"))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    private String createAdminAndGetToken(String email) throws Exception {
        adminRepository.save(new Admin("Administrator", email, passwordEncoder.encode("secret123")));
        return loginAndGetToken(email);
    }

    @Test
    void createShouldReturn201WithPendingStatusAndHidePassword() throws Exception {
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("jane@fandza.co.mz"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@fandza.co.mz"))
                .andExpect(jsonPath("$.accountStatus").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createShouldReturn409WhenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("jane@fandza.co.mz"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("jane@fandza.co.mz"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void createShouldReturn400WithFieldErrorsWhenRequestIsInvalid() throws Exception {
        DriverRequest invalid = new DriverRequest(
                "", "not-an-email", "secret123", "+258840000000", "L-123",
                OffsetDateTime.now(BUSINESS_ZONE).plusYears(1));

        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fullName").value(notNullValue()))
                .andExpect(jsonPath("$.fieldErrors.email").value(notNullValue()));
    }

    @Test
    void getByIdShouldReturn200Or404() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        String token = loginAndGetToken("jane@fandza.co.mz");

        mockMvc.perform(get("/api/v1/drivers/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(get("/api/v1/drivers/{id}", MISSING_ID)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(get("/api/v1/drivers/{id}", "9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findAllShouldReturnPaginatedDrivers() throws Exception {
        createDriverAndGetId("jane@fandza.co.mz");
        String token = loginAndGetToken("jane@fandza.co.mz");
        driverRepository.save(new Driver("Ana Bila", "ana@fandza.co.mz", "hash"));

        mockMvc.perform(get("/api/v1/drivers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void updateStatusShouldActivateDriverAndRejectInvalidTransition() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        String token = createAdminAndGetToken("admin@fandza.co.mz");

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"));

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"PENDING\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void updateStatusShouldReturn400WhenStatusIsNull() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        String token = createAdminAndGetToken("admin@fandza.co.mz");

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status").value(notNullValue()));
    }

    @Test
    void updateStatusShouldReturn403WhenRequesterIsNotAdmin() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        String driverToken = loginAndGetToken("jane@fandza.co.mz");

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .header("Authorization", "Bearer " + driverToken)
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
}
