package com.supdevinci.lootopia.dto;

import com.supdevinci.lootopia.model.Artefact;

import java.time.Instant;
import java.util.Locale;

public record ArtefactDto(
        String id,
        String name,
        String description,
        String rarity,
        String category,
        String imageUrl,
        Integer xpBonus,
        String originHuntId,
        String ownerId,
        Instant acquiredAt,
        boolean isTradable
) {
    public static ArtefactDto from(Artefact artefact) {
        return new ArtefactDto(
                artefact.getId().toString(),
                artefact.getName(),
                artefact.getDescription(),
                artefact.getRarity().name().toLowerCase(Locale.ROOT),
                artefact.getCategory().name().toLowerCase(Locale.ROOT),
                artefact.getImageUrl(),
                artefact.getXpBonus(),
                null,
                artefact.getOwner().getId() == null ? null : artefact.getOwner().getId().toString(),
                artefact.getCreatedAt(),
                artefact.isTradable()
        );
    }
}
