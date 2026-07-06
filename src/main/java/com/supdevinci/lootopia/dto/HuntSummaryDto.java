package com.supdevinci.lootopia.dto;

import java.time.Instant;

public record HuntSummaryDto(
        Long id,
        String title,
        String description,
        String difficulty,
        String duration,
        String theme,
        Integer maxParticipants,
        String status,
        String creatorName,
        Integer stepsCount,
        Integer rewardsCount,
        Double latitude,
        Double longitude,
        Instant createdAt
) {
}
