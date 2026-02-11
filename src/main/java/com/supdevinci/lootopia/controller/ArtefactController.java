package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.controller.dto.ArtefactResponse;
import com.supdevinci.lootopia.model.Artefact;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.ArtefactRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artefacts")
@RequiredArgsConstructor
@Tag(name = "Artefacts", description = "Endpoints pour l'inventaire d'artefacts")
public class ArtefactController {

    private final ArtefactRepository artefactRepository;

    @Operation(summary = "Mon inventaire d'artefacts")
    @GetMapping("/mine")
    public ResponseEntity<List<ArtefactResponse>> getMyArtefacts(
            @AuthenticationPrincipal User user
    ) {
        List<ArtefactResponse> artefacts = artefactRepository.findByOwnerId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(artefacts);
    }

    private ArtefactResponse toResponse(Artefact artefact) {
        return ArtefactResponse.builder()
                .id(artefact.getId())
                .name(artefact.getName())
                .imageUrl(artefact.getImageUrl())
                .rarity(artefact.getRarity())
                .obtainedAt(artefact.getCreatedAt())
                .build();
    }
}
