package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.controller.dto.ParticipationResponse;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
@Tag(name = "Participations", description = "Endpoints pour la participation aux chasses")
public class ParticipationController {

    private final ParticipationService participationService;

    @Operation(summary = "Rejoindre une chasse")
    @PostMapping("/{huntId}/join")
    public ResponseEntity<ParticipationResponse> joinHunt(
            @PathVariable Long huntId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(participationService.joinHunt(huntId, user));
    }

    @Operation(summary = "Valider une étape")
    @PutMapping("/{id}/validate-step")
    public ResponseEntity<ParticipationResponse> validateStep(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(participationService.validateStep(id, user));
    }

    @Operation(summary = "Abandonner une chasse")
    @PutMapping("/{id}/abandon")
    public ResponseEntity<ParticipationResponse> abandonHunt(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(participationService.abandonHunt(id, user));
    }

    @Operation(summary = "Mes participations")
    @GetMapping("/mine")
    public ResponseEntity<List<ParticipationResponse>> getMyParticipations(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(participationService.getMyParticipations(user));
    }

    @Operation(summary = "Détail d'une participation")
    @GetMapping("/{id}")
    public ResponseEntity<ParticipationResponse> getParticipation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(participationService.getParticipation(id, user));
    }
}
