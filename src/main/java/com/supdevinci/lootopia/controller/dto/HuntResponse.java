package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HuntResponse {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private LocalTime duration;
    private String theme;
    private Integer maxParticipants;
    private String status;
    private String creatorName;
    private LocalDateTime createdAt;
    private List<StepResponse> steps;
    private List<RewardResponse> rewards;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StepResponse {
        private Long id;
        private Integer orderIndex;
        private String title;
        private String description;
        private Double latitude;
        private Double longitude;
        private Integer radius;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RewardResponse {
        private Long id;
        private String name;
        private String imageUrl;
        private Long winnerId;
    }
}
