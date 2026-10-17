package com.fandza.app.Fandza.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.fandza.app.Fandza.dtos.driver.DriverRequest;
import com.fandza.app.Fandza.entity.Driver;
import com.fandza.app.Fandza.enums.AccountStatus;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DriverControllerTest {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private DriverRequest validRequest(String email) {
        return new DriverRequest(
                "Jane Doe",
                email,
                "secret123",
                "+258840000000",
                "L-123",
                OffsetDateTime.now(BUSINESS_ZONE).plusYears(1));
    }

    private Long createDriverAndGetId(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(email))))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
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
        Long id = createDriverAndGetId("jane@fandza.co.mz");

        mockMvc.perform(get("/api/v1/drivers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(get("/api/v1/drivers/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findAllShouldReturnPaginatedDrivers() throws Exception {
        driverRepository.save(new Driver("Ana Bila", "ana@fandza.co.mz", "hash"));
        driverRepository.save(new Driver("Bento Chirindza", "bento@fandza.co.mz", "hash"));

        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void updateStatusShouldActivateDriverAndRejectInvalidTransition() throws Exception {
        Long id = createDriverAndGetId("jane@fandza.co.mz");

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"));

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"PENDING\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void updateStatusShouldReturn400WhenStatusIsNull() throws Exception {
        Long id = createDriverAndGetId("jane@fandza.co.mz");

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status").value(notNullValue()));
    }
}
