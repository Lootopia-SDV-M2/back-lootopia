package com.supdevinci.lootopia.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ListArtefactRequest {
    private Long artefactId;
    private BigDecimal price;
    private String type;
}
