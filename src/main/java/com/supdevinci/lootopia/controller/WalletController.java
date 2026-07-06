package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.dto.WalletDto;
import com.supdevinci.lootopia.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @GetMapping("/me")
    public WalletDto getCurrentWallet(@AuthenticationPrincipal User user) {
        return new WalletDto(
                String.valueOf(user.getId()),
                user.getBalance(),
                Instant.now()
        );
    }
}
