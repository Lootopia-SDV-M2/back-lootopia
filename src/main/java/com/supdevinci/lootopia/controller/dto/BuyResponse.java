package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyResponse {
    private String message;
    private Long transactionId;
}
