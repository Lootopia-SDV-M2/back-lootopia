package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.dto.auth.AuthResponse;
import com.supdevinci.lootopia.dto.auth.LoginRequest;
import com.supdevinci.lootopia.dto.auth.RegisterRequest;
import com.supdevinci.lootopia.model.Role;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    private final AuthService authService = new AuthService(
            userRepository,
            passwordEncoder,
            jwtService,
            authenticationManager
    );

    @Test
    void registersUserWithFrontendRoleAndJwt() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(anyMap(), any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(new RegisterRequest(
                "alice",
                "alice@example.com",
                "Secret123!",
                "CHERCHEUR"
        ));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.user().username()).isEqualTo("alice");
        assertThat(response.user().email()).isEqualTo("alice@example.com");
        assertThat(response.user().role()).isEqualTo("CHERCHEUR");
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest(
                "alice",
                "alice@example.com",
                "Secret123!",
                "CHERCHEUR"
        ))).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void logsInWithEmailAsUsernamePayload() {
        User user = new User();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("hashed-password");
        user.setRole(Role.CHERCHEUR);

        when(userRepository.findByUsernameOrEmail("alice@example.com", "alice@example.com"))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyMap(), any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("alice@example.com", "Secret123!"));

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("alice@example.com", "Secret123!")
        );
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.user().username()).isEqualTo("alice");
    }
}
