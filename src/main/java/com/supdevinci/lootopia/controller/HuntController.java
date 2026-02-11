package com.supdevinci.lootopia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supdevinci.lootopia.controller.dto.*;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.service.FileStorageService;
import com.supdevinci.lootopia.service.HuntService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/hunts")
@RequiredArgsConstructor
@Tag(name = "Hunts", description = "Endpoints pour la gestion des chasses au trésor")
public class HuntController {

    private final HuntService huntService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Créer une nouvelle chasse au trésor")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HuntResponse> createHunt(
            @RequestPart("hunt") String huntJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal User creator
    ) throws Exception {
        CreateHuntRequest request = objectMapper.readValue(huntJson, CreateHuntRequest.class);
        HuntResponse response = huntService.createHunt(request, creator);

        // Upload reward images
        if (images != null) {
            for (int i = 0; i < images.size() && i < response.getRewards().size(); i++) {
                String imageUrl = fileStorageService.storeFile(images.get(i));
                huntService.updateRewardImage(response.getId(), i, imageUrl);
                response.getRewards().get(i).setImageUrl(imageUrl);
            }
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister les chasses publiées")
    @GetMapping
    public ResponseEntity<List<HuntSummaryResponse>> getPublishedHunts() {
        return ResponseEntity.ok(huntService.getAllPublishedHunts());
    }

    @Operation(summary = "Détail d'une chasse")
    @GetMapping("/{id}")
    public ResponseEntity<HuntResponse> getHuntById(@PathVariable Long id) {
        return ResponseEntity.ok(huntService.getHuntById(id));
    }

    @Operation(summary = "Mes chasses (créateur)")
    @GetMapping("/mine")
    public ResponseEntity<List<HuntSummaryResponse>> getMyHunts(
            @AuthenticationPrincipal User creator
    ) {
        return ResponseEntity.ok(huntService.getHuntsByCreator(creator.getId()));
    }

    @Operation(summary = "Publier une chasse")
    @PutMapping("/{id}/publish")
    public ResponseEntity<HuntResponse> publishHunt(
            @PathVariable Long id,
            @AuthenticationPrincipal User creator
    ) {
        return ResponseEntity.ok(huntService.publishHunt(id, creator));
    }
}
