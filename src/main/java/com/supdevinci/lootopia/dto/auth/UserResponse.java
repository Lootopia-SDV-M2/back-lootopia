package com.supdevinci.lootopia.dto.auth;

import com.supdevinci.lootopia.model.User;

public record UserResponse(
        Long id,
        String username,
        String email,
        String siret,
        String role,
        Long balance
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getSiret(),
                user.getRole().name(),
                user.getBalance()
        );
    }
}
