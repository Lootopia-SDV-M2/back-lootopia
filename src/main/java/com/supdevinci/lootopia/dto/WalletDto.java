package com.supdevinci.lootopia.dto;

import java.time.Instant;

public record WalletDto(
        String userId,
        Long balancePol,
        Instant updatedAt
) {
}
