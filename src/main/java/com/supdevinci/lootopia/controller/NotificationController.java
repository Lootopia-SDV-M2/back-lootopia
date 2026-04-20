package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.controller.dto.NotificationResponse;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints pour les notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Mes notifications")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getNotifications(user));
    }

    @Operation(summary = "Marquer une notification comme lue")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        notificationService.markRead(id, user);
        return ResponseEntity.ok().build();
    }
}
