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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
    name = "Authentication",
    description = "Endpoints pour l'inscription et l'authentification"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Inscription d'un utilisateur",
        security={},
        description = "Crée un utilisateur et retourne un JWT",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Utilisateur créé",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Données invalides"
            )
        }
    )
    @PostMapping("/register")
    @SecurityRequirements({})
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur et retourne un JWT",
        security={},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Authentification réussie",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Identifiants invalides"
            )
        }
    )
    @PostMapping("/login")
    @SecurityRequirements({})
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
