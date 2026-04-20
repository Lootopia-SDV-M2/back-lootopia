package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.controller.dto.RedeemRequest;
import com.supdevinci.lootopia.controller.dto.RedeemResponse;
import com.supdevinci.lootopia.controller.dto.VoucherResponse;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.model.enums.Role;
import com.supdevinci.lootopia.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Tag(name = "Vouchers", description = "Endpoints pour les bons d'achat")
public class VoucherController {
    private final VoucherService voucherService;

    @Operation(summary = "Mes bons d'achat")
    @GetMapping("/mine")
    public ResponseEntity<List<VoucherResponse>> getMyVouchers(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(voucherService.getMyVouchers(user));
    }

    @Operation(summary = "Utiliser un bon d'achat")
    @PostMapping("/redeem")
    public ResponseEntity<RedeemResponse> redeem(@RequestBody RedeemRequest request,
            @AuthenticationPrincipal User user) {
        if (user.getRole() != Role.ORGANISATEUR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(voucherService.redeem(request.getCode()));
    }
}
