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
public class ParticipationResponse {
    private Long id;
    private Long huntId;
    private String huntTitle;
    private String huntDifficulty;
    private LocalTime huntDuration;
    private Integer huntReward;
    private String creatorName;
    private Integer currentStepIndex;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<StepInfo> steps;
    private List<RewardInfo> rewards;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StepInfo {
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
    public static class RewardInfo {
        private Long id;
        private String name;
        private String imageUrl;
        private Long winnerId;
    }
}
