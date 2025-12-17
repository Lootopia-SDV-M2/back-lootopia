package com.supdevinci.lootopia.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supdevinci.lootopia.controller.dto.AuthRequest;
import com.supdevinci.lootopia.controller.dto.AuthResponse;
import com.supdevinci.lootopia.controller.dto.RegisterRequest;
import com.supdevinci.lootopia.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}