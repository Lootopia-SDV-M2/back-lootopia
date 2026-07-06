package com.supdevinci.lootopia.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 2, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        String role,
        @Pattern(regexp = "^$|^[\\d\\s]{14,20}$", message = "SIRET must contain 14 digits") String siret
) {
}
