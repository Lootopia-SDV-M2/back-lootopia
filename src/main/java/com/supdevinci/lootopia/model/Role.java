package com.supdevinci.lootopia.model;

public enum Role {
    CHERCHEUR,
    ORGANISATEUR,
    ADMIN;

    public static Role fromApiValue(String value) {
        if (value == null || value.isBlank()) {
            return CHERCHEUR;
        }
        return Role.valueOf(value.trim().toUpperCase());
    }
}
