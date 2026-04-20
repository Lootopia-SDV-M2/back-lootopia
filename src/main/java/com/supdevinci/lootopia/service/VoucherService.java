package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.controller.dto.RedeemResponse;
import com.supdevinci.lootopia.controller.dto.VoucherResponse;
import com.supdevinci.lootopia.model.Reward;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.model.Voucher;
import com.supdevinci.lootopia.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public Voucher createVoucher(Reward reward, User winner) {
        Voucher v = new Voucher();
        v.setCode(UUID.randomUUID().toString());
        v.setReward(reward);
        v.setWinner(winner);
        v.setActive(true);
        return voucherRepository.save(v);
    }

    public List<VoucherResponse> getMyVouchers(User user) {
        return voucherRepository.findByWinnerIdOrderByCreatedAtDesc(user.getId())
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RedeemResponse redeem(String code) {
        Voucher v = voucherRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Bon d'achat introuvable"));
        if (!v.isActive()) throw new RuntimeException("Ce bon d'achat a déjà été utilisé");
        v.setActive(false);
        v.setUsedAt(LocalDateTime.now());
        voucherRepository.save(v);
        return new RedeemResponse("Bon validé avec succès", v.getCode(), v.getReward().getName());
    }

    private VoucherResponse toResponse(Voucher v) {
        VoucherResponse r = new VoucherResponse();
        r.setId(v.getId());
        r.setCode(v.getCode());
        r.setActive(v.isActive());
        r.setRewardName(v.getReward().getName());
        r.setRewardImageUrl(v.getReward().getImageUrl());
        r.setHuntTitle(v.getReward().getHunt().getTitle());
        r.setObtainedAt(v.getCreatedAt().toString());
        return r;
    }
}
