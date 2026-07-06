package com.supdevinci.lootopia.dto.auth;

public record AuthResponse(
        String token,
        String username,
        String email,
        String role,
        UserResponse user
) {
    public AuthResponse(String token, UserResponse user) {
        this(token, user.username(), user.email(), user.role(), user);
    }
}
