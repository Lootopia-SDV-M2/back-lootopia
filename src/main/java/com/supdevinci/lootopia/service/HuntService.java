package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.controller.dto.*;
import com.supdevinci.lootopia.model.*;
import com.supdevinci.lootopia.model.enums.HuntStatus;
import com.supdevinci.lootopia.repository.HuntRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HuntService {

    private final HuntRepository huntRepository;

    @Transactional
    public HuntResponse createHunt(CreateHuntRequest request, User creator) {
        // Validate minimum 2 steps
        if (request.getSteps() == null || request.getSteps().size() < 2) {
            throw new IllegalArgumentException("Une chasse doit avoir au minimum 2 étapes");
        }

        // Validate rewards count matches maxParticipants
        if (request.getRewards() == null || request.getRewards().size() != request.getMaxParticipants()) {
            throw new IllegalArgumentException("Le nombre de cadeaux doit correspondre au nombre maximum de participants");
        }

        Hunt hunt = new Hunt();
        hunt.setTitle(request.getTitle());
        hunt.setDescription(request.getDescription());
        hunt.setDifficulty(request.getDifficulty());
        hunt.setDuration(request.getDuration());
        hunt.setTheme(request.getTheme());
        hunt.setMaxParticipants(request.getMaxParticipants());
        hunt.setCreator(creator);
        hunt.setStatus(HuntStatus.BROUILLON);

        // Create steps
        List<Step> steps = new ArrayList<>();
        for (StepRequest stepReq : request.getSteps()) {
            Step step = new Step();
            step.setHunt(hunt);
            step.setOrderIndex(stepReq.getOrderIndex());
            step.setTitle(stepReq.getTitle());
            step.setDescription(stepReq.getDescription());
            step.setLatitude(stepReq.getLatitude());
            step.setLongitude(stepReq.getLongitude());
            step.setRadius(stepReq.getRadius() != null ? stepReq.getRadius() : 20);
            steps.add(step);
        }
        hunt.setSteps(steps);

        // Create rewards
        List<Reward> rewards = new ArrayList<>();
        for (RewardRequest rewardReq : request.getRewards()) {
            Reward reward = new Reward();
            reward.setHunt(hunt);
            reward.setName(rewardReq.getName());
            rewards.add(reward);
        }
        hunt.setRewards(rewards);

        Hunt savedHunt = huntRepository.save(hunt);
        return toHuntResponse(savedHunt);
    }

    @Transactional
    public HuntResponse publishHunt(Long huntId, User creator) {
        Hunt hunt = huntRepository.findById(huntId)
                .orElseThrow(() -> new RuntimeException("Chasse introuvable"));

        if (!hunt.getCreator().getId().equals(creator.getId())) {
            throw new RuntimeException("Vous n'êtes pas le créateur de cette chasse");
        }

        if (hunt.getStatus() != HuntStatus.BROUILLON) {
            throw new RuntimeException("Seule une chasse en brouillon peut être publiée");
        }

        hunt.setStatus(HuntStatus.PUBLIE);
        Hunt savedHunt = huntRepository.save(hunt);
        return toHuntResponse(savedHunt);
    }

    public HuntResponse getHuntById(Long id) {
        Hunt hunt = huntRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chasse introuvable"));
        return toHuntResponse(hunt);
    }

    public List<HuntSummaryResponse> getHuntsByCreator(Long creatorId) {
        return huntRepository.findByCreatorId(creatorId).stream()
                .map(this::toHuntSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<HuntSummaryResponse> getAllPublishedHunts() {
        return huntRepository.findByStatus(HuntStatus.PUBLIE).stream()
                .map(this::toHuntSummaryResponse)
                .collect(Collectors.toList());
    }

    public void updateRewardImage(Long huntId, int rewardIndex, String imageUrl) {
        Hunt hunt = huntRepository.findById(huntId)
                .orElseThrow(() -> new RuntimeException("Chasse introuvable"));
        List<Reward> rewards = hunt.getRewards();
        if (rewardIndex >= 0 && rewardIndex < rewards.size()) {
            rewards.get(rewardIndex).setImageUrl(imageUrl);
            huntRepository.save(hunt);
        }
    }

    private HuntResponse toHuntResponse(Hunt hunt) {
        List<HuntResponse.StepResponse> stepResponses = hunt.getSteps() != null
                ? hunt.getSteps().stream().map(step -> HuntResponse.StepResponse.builder()
                    .id(step.getId())
                    .orderIndex(step.getOrderIndex())
                    .title(step.getTitle())
                    .description(step.getDescription())
                    .latitude(step.getLatitude())
                    .longitude(step.getLongitude())
                    .radius(step.getRadius())
                    .build())
                .collect(Collectors.toList())
                : new ArrayList<>();

        List<HuntResponse.RewardResponse> rewardResponses = hunt.getRewards() != null
                ? hunt.getRewards().stream().map(reward -> HuntResponse.RewardResponse.builder()
                    .id(reward.getId())
                    .name(reward.getName())
                    .imageUrl(reward.getImageUrl())
                    .winnerId(reward.getWinner() != null ? reward.getWinner().getId() : null)
                    .build())
                .collect(Collectors.toList())
                : new ArrayList<>();

        return HuntResponse.builder()
                .id(hunt.getId())
                .title(hunt.getTitle())
                .description(hunt.getDescription())
                .difficulty(hunt.getDifficulty())
                .duration(hunt.getDuration())
                .theme(hunt.getTheme())
                .maxParticipants(hunt.getMaxParticipants())
                .status(hunt.getStatus().name())
                .creatorName(hunt.getCreator().getNom())
                .createdAt(hunt.getCreatedAt())
                .steps(stepResponses)
                .rewards(rewardResponses)
                .build();
    }

    private HuntSummaryResponse toHuntSummaryResponse(Hunt hunt) {
        return HuntSummaryResponse.builder()
                .id(hunt.getId())
                .title(hunt.getTitle())
                .description(hunt.getDescription())
                .difficulty(hunt.getDifficulty())
                .duration(hunt.getDuration())
                .theme(hunt.getTheme())
                .maxParticipants(hunt.getMaxParticipants())
                .status(hunt.getStatus().name())
                .creatorName(hunt.getCreator().getNom())
                .stepsCount(hunt.getSteps() != null ? hunt.getSteps().size() : 0)
                .rewardsCount(hunt.getRewards() != null ? hunt.getRewards().size() : 0)
                .createdAt(hunt.getCreatedAt())
                .build();
    }
}
