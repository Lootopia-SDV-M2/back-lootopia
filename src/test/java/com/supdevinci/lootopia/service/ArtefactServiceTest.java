package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.dto.ArtefactDto;
import com.supdevinci.lootopia.model.Artefact;
import com.supdevinci.lootopia.model.ArtefactCategory;
import com.supdevinci.lootopia.model.Rarity;
import com.supdevinci.lootopia.model.Role;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.ArtefactRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArtefactServiceTest {

    private final ArtefactRepository artefactRepository = mock(ArtefactRepository.class);
    private final ArtefactService artefactService = new ArtefactService(artefactRepository);

    @Test
    void returnsCurrentUserInventoryAsDtos() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("alice");
        owner.setEmail("alice@example.com");
        owner.setPassword("hashed-password");
        owner.setRole(Role.CHERCHEUR);

        Artefact artefact = new Artefact();
        artefact.setId(10L);
        artefact.setOwner(owner);
        artefact.setName("Ancient Compass");
        artefact.setDescription("Points toward hidden rewards.");
        artefact.setCategory(ArtefactCategory.HISTORY);
        artefact.setRarity(Rarity.RARE);
        artefact.setImageUrl("https://example.com/compass.png");
        artefact.setXpBonus(25);
        artefact.setTradable(true);
        artefact.setCreatedAt(Instant.parse("2026-07-06T10:15:30Z"));

        when(artefactRepository.findByOwner(owner)).thenReturn(List.of(artefact));

        List<ArtefactDto> inventory = artefactService.getInventoryFor(owner);

        assertThat(inventory).containsExactly(new ArtefactDto(
                "10",
                "Ancient Compass",
                "Points toward hidden rewards.",
                "rare",
                "history",
                "https://example.com/compass.png",
                25,
                null,
                "1",
                Instant.parse("2026-07-06T10:15:30Z"),
                true
        ));
    }
}
