package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.dto.ArtefactDto;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.service.ArtefactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artefacts")
@RequiredArgsConstructor
public class ArtefactController {

    private final ArtefactService artefactService;

    @GetMapping("/mine")
    public ResponseEntity<List<ArtefactDto>> getMyArtefacts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(artefactService.getInventoryFor(user));
    }
}
