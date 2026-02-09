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
public class HuntSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private String duration;
    private String theme;
    private Integer maxParticipants;
    private String status;
    private String creatorName;
    private int stepsCount;
    private int rewardsCount;
    private LocalDateTime createdAt;
}
