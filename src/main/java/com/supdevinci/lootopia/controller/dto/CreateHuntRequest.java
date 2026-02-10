package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateHuntRequest {
    private String title;
    private String description;
    private String difficulty;
    private LocalTime duration;
    private String theme;
    private Integer maxParticipants;
    private List<StepRequest> steps;
    private List<RewardRequest> rewards;
}
