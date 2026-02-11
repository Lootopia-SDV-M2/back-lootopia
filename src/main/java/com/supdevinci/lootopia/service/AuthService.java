package com.supdevinci.lootopia.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.supdevinci.lootopia.controller.dto.AuthRequest;
import com.supdevinci.lootopia.controller.dto.AuthResponse;
import com.supdevinci.lootopia.controller.dto.RegisterRequest;
import com.supdevinci.lootopia.model.enums.Role;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = new User();
        user.setNom(request.getUsername());
        user.setPrenom("");
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = "ORGANISATEUR".equalsIgnoreCase(request.getRole()) ? Role.ORGANISATEUR : Role.CHERCHEUR;
        user.setRole(role);
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .username(user.getNom())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .username(user.getNom())
                .build();
    }
}