package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StepRequest {
    private Integer orderIndex;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private Integer radius;
}
