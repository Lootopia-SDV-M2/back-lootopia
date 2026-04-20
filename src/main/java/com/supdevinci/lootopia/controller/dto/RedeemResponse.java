package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedeemResponse {
    private String message;
    private String voucherCode;
    private String rewardName;
}
