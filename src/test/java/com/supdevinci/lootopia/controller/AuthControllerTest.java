package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void registerCreatesUserWithFrontendRoleAndReturnsJwt() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "alice",
                                  "email": "alice@example.com",
                                  "password": "secret123",
                                  "role": "ORGANISATEUR",
                                  "siret": "123 456 789 00012"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("ORGANISATEUR"))
                .andExpect(jsonPath("$.user.siret").value("12345678900012"));

        User savedUser = userRepository.findByUsername("alice").orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo("alice@example.com");
        assertThat(savedUser.getRole().name()).isEqualTo("ORGANISATEUR");
        assertThat(savedUser.getSiret()).isEqualTo("12345678900012");
        assertThat(passwordEncoder.matches("secret123", savedUser.getPassword())).isTrue();
    }

    @Test
    void loginAcceptsEmailAsUsernameAndReturnsJwt() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "bob",
                                  "email": "bob@example.com",
                                  "password": "secret123",
                                  "role": "CHERCHEUR",
                                  "siret": ""
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "bob@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"))
                .andExpect(jsonPath("$.role").value("CHERCHEUR"));
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
