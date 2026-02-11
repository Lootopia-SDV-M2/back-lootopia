package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtefactResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String rarity;
    private LocalDateTime obtainedAt;
}
