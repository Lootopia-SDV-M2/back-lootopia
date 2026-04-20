package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {
    private Long id;
    private String code;
    private boolean active;
    private String rewardName;
    private String rewardImageUrl;
    private String huntTitle;
    private String obtainedAt;
}
