package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketListingResponse {
    private Long id;
    private ArtefactInfo artefact;
    private String sellerName;
    private BigDecimal price;
    private String type;
    private String status;
    private String createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArtefactInfo {
        private Long id;
        private String name;
        private String imageUrl;
        private String rarity;
    }
}
