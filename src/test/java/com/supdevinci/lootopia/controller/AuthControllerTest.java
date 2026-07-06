package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void registerOrganizerStoresNormalizedSiret() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "Musee Demo",
                                  "email": "museum@example.com",
                                  "password": "secret123",
                                  "role": "ORGANISATEUR",
                                  "siret": "123 456 789 00012"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andExpect(jsonPath("$.username").value("Musee Demo"))
                .andExpect(jsonPath("$.role").value("ORGANISATEUR"))
                .andExpect(jsonPath("$.siret").value("12345678900012"));

        User savedUser = userRepository.findByEmail("museum@example.com").orElseThrow();
        assertThat(savedUser.getNom()).isEqualTo("Musee Demo");
        assertThat(savedUser.getSiret()).isEqualTo("12345678900012");
        assertThat(savedUser.getRole().name()).isEqualTo("ORGANISATEUR");
    }

    @Test
    void rejectOrganizerWithoutSiret() throws Exception {
        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "Musee Demo",
                                  "email": "museum@example.com",
                                  "password": "secret123",
                                  "role": "ORGANISATEUR"
                                }
                                """)))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SIRET is required");
    }
}
