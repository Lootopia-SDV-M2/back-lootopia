package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.dto.auth.AuthResponse;
import com.supdevinci.lootopia.dto.auth.LoginRequest;
import com.supdevinci.lootopia.dto.auth.RegisterRequest;
import com.supdevinci.lootopia.dto.auth.UserResponse;
import com.supdevinci.lootopia.model.Role;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.fromApiValue(request.role()));
        user.setBalance(0L);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return toAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsernameOrEmail(request.username(), request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(
                Map.of("role", user.getRole().name()),
                user
        );
        return new AuthResponse(token, UserResponse.from(user));
    }
}
