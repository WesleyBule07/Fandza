package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.fandza.ccs.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.repository.LegalGuardianRepository;
import com.fandza.ccs.Fandza.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GuardianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String MISSING_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LegalGuardianRepository legalGuardianRepository;

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
        legalGuardianRepository.deleteAll();
        userRepository.deleteAll();
    }

    private GuardianRequest validRequest(String email) {
        return new GuardianRequest("Maria Sitoe", ContactType.PHONE, "840000000");
    }

    private String createGuardianAndGetId(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/guardians")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(email))))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asText();
    }


    @Test
    void createShouldReturn201WithoutPassword() throws Exception {
        mockMvc.perform(post("/api/v1/guardians")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("maria@fandza.co.mz"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.fullName").value("Maria Sitoe"))
                .andExpect(jsonPath("$.contactValue").value("+258840000000"))
                .andExpect(jsonPath("$.contactValue").value("+258840000000"))
                .andExpect(jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.userType").doesNotExist());
    }

    @Test
    void createShouldReturn409WhenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/v1/guardians")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("maria@fandza.co.mz"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/guardians")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("maria@fandza.co.mz"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void createShouldReturn400WithFieldErrorsWhenRequestIsInvalid() throws Exception {
        GuardianRequest invalid = new GuardianRequest("", ContactType.PHONE, "");

        mockMvc.perform(post("/api/v1/guardians")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fullName").value(notNullValue()))
                .andExpect(jsonPath("$.fieldErrors.contactValue").value(notNullValue()));
    }

    @Test
    void createShouldReturn409WhenEmailBelongsToDriver() throws Exception {
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(APPLICATION_JSON)
                        .content("{\"fullName\":\"Jane Doe\",\"contactType\":\"EMAIL\","
                                + "\"contactValue\":\"shared@fandza.co.mz\",\"licenseNumber\":\"L-1\","
                                + "\"licenseExpiry\":\"2030-12-31T00:00:00Z\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/guardians")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GuardianRequest("Maria Sitoe", ContactType.EMAIL, "shared@fandza.co.mz"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void getByIdShouldRequireAuthenticationAndReturn200Or404() throws Exception {
        String id = createGuardianAndGetId("maria@fandza.co.mz");
        
        mockMvc.perform(get("/api/v1/guardians/{id}", id))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/guardians/{id}", id)
                        .with(guardianJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(get("/api/v1/guardians/{id}", MISSING_ID)
                        .with(guardianJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findAllShouldReturnPaginatedGuardians() throws Exception {
        createGuardianAndGetId("maria@fandza.co.mz");
        
        mockMvc.perform(get("/api/v1/guardians")
                        .with(guardianJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content.length()").value(1));
    }


    private RequestPostProcessor guardianJwt() {
        return jwt();
    }
}
