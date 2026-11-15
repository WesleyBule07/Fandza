package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.fandza.ccs.Fandza.dtos.driver.DriverRequest;
import com.fandza.ccs.Fandza.entity.Driver;
import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.repository.UserRepository;
import com.fandza.ccs.Fandza.repository.DriverRepository;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DriverControllerTest {

        private static final String MISSING_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    private DriverRequest validRequest(String email) {
        return new DriverRequest(
                "Jane Doe",
                ContactType.EMAIL,
                email,
                "L-123",
                Instant.now().plusSeconds(60 * 60 * 24 * 365));
    }

    private String createDriverAndGetId(String email) throws Exception {
        Driver driver = new Driver("Jane Doe", ContactType.EMAIL, email);
        driver.setLicenseNumber("L-123");
        driverRepository.save(driver);
        return driver.getId().toString();
    }



    @Test
    void getByIdShouldReturn200Or404() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        
        mockMvc.perform(get("/api/v1/drivers/{id}", id)
                        .with(guardianJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(get("/api/v1/drivers/{id}", MISSING_ID)
                        .with(guardianJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(get("/api/v1/drivers/{id}", "9999")
                        .with(guardianJwt()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findAllShouldReturnPaginatedDrivers() throws Exception {
        createDriverAndGetId("jane@fandza.co.mz");
                driverRepository.save(new Driver("Ana Bila", ContactType.EMAIL, "ana@fandza.co.mz"));

        mockMvc.perform(get("/api/v1/drivers")
                        .with(guardianJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void updateStatusShouldActivateDriverAndRejectInvalidTransition() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        
        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .with(adminJwt())
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"));

        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .with(adminJwt())
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"PENDING\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void updateStatusShouldReturn400WhenStatusIsNull() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        
        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .with(guardianJwt())
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status").value(notNullValue()));
    }

    @Test
    void updateStatusShouldReturn403WhenRequesterIsNotAdmin() throws Exception {
        String id = createDriverAndGetId("jane@fandza.co.mz");
        
        mockMvc.perform(patch("/api/v1/drivers/{id}/status", id)
                        .with(guardianJwt())
                        .contentType(APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }


    private RequestPostProcessor guardianJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_DRIVER"));
    }

    private RequestPostProcessor adminJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
