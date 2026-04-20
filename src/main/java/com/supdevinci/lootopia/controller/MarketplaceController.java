package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.controller.dto.BuyResponse;
import com.supdevinci.lootopia.controller.dto.ListArtefactRequest;
import com.supdevinci.lootopia.controller.dto.MarketListingResponse;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.service.MarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
@Tag(name = "Marketplace", description = "Endpoints pour la gestion du marche aux artefacts")
public class MarketplaceController {
    private final MarketplaceService marketplaceService;

    @Operation(summary = "Lister les annonces actives")
    @GetMapping
    public ResponseEntity<List<MarketListingResponse>> getListings() {
        return ResponseEntity.ok(marketplaceService.getActiveListings());
    }

    @Operation(summary = "Mes annonces")
    @GetMapping("/mine")
    public ResponseEntity<List<MarketListingResponse>> getMyListings(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(marketplaceService.getMyListings(user));
    }

    @Operation(summary = "Deposr une annonce")
    @PostMapping("/list")
    public ResponseEntity<MarketListingResponse> listArtefact(
            @RequestBody ListArtefactRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(marketplaceService.listArtefact(req, user));
    }

    @Operation(summary = "Acheter un artefact")
    @PostMapping("/{id}/buy")
    public ResponseEntity<BuyResponse> buy(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(marketplaceService.buy(id, user));
    }
}
