package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.controller.dto.ParticipationResponse;
import com.supdevinci.lootopia.model.*;
import com.supdevinci.lootopia.model.enums.ParticipationStatus;
import com.supdevinci.lootopia.repository.ArtefactRepository;
import com.supdevinci.lootopia.repository.HuntRepository;
import com.supdevinci.lootopia.repository.ParticipationRepository;
import com.supdevinci.lootopia.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final HuntRepository huntRepository;
    private final RewardRepository rewardRepository;
    private final ArtefactRepository artefactRepository;

    @Transactional
    public ParticipationResponse joinHunt(Long huntId, User user) {
        Hunt hunt = huntRepository.findById(huntId)
                .orElseThrow(() -> new RuntimeException("Chasse introuvable"));

        // Check if user already has an active participation for this hunt
        if (participationRepository.existsByUserIdAndHuntIdAndStatus(
                user.getId(), huntId, ParticipationStatus.EN_COURS)) {
            throw new RuntimeException("Vous participez déjà à cette chasse");
        }

        // Check available rewards (slots)
        long completedCount = participationRepository.countByHuntIdAndStatus(
                huntId, ParticipationStatus.TERMINE);
        if (completedCount >= hunt.getMaxParticipants()) {
            throw new RuntimeException("Cette chasse est complète, plus de places disponibles");
        }

        Participation participation = new Participation();
        participation.setUser(user);
        participation.setHunt(hunt);
        participation.setCurrentStepIndex(0);
        participation.setStatus(ParticipationStatus.EN_COURS);

        Participation saved = participationRepository.save(participation);
        return toResponse(saved);
    }

    @Transactional
    public ParticipationResponse validateStep(Long participationId, User user) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation introuvable"));

        if (!participation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cette participation ne vous appartient pas");
        }

        if (participation.getStatus() != ParticipationStatus.EN_COURS) {
            throw new RuntimeException("Cette participation n'est plus en cours");
        }

        Hunt hunt = participation.getHunt();
        int nextIndex = participation.getCurrentStepIndex() + 1;
        boolean isLastStep = nextIndex >= hunt.getSteps().size();

        if (isLastStep) {
            // Hunt completed!
            participation.setStatus(ParticipationStatus.TERMINE);

            // Assign a reward to the winner
            assignReward(hunt, user);
        } else {
            participation.setCurrentStepIndex(nextIndex);
        }

        Participation saved = participationRepository.save(participation);
        return toResponse(saved);
    }

    @Transactional
    public ParticipationResponse abandonHunt(Long participationId, User user) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation introuvable"));

        if (!participation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cette participation ne vous appartient pas");
        }

        if (participation.getStatus() != ParticipationStatus.EN_COURS) {
            throw new RuntimeException("Cette participation n'est plus en cours");
        }

        participation.setStatus(ParticipationStatus.ABANDONNE);
        Participation saved = participationRepository.save(participation);
        return toResponse(saved);
    }

    public List<ParticipationResponse> getMyParticipations(User user) {
        return participationRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ParticipationResponse getParticipation(Long participationId, User user) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation introuvable"));

        if (!participation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cette participation ne vous appartient pas");
        }

        return toResponse(participation);
    }

    /**
     * Assign the first available (unclaimed) reward to the winner
     * and create an Artefact in their inventory.
     */
    private void assignReward(Hunt hunt, User winner) {
        List<Reward> rewards = hunt.getRewards();
        if (rewards == null || rewards.isEmpty()) return;

        // Find first reward without a winner
        Reward availableReward = rewards.stream()
                .filter(r -> r.getWinner() == null)
                .findFirst()
                .orElse(null);

        if (availableReward == null) return;

        // Assign the reward to the winner
        availableReward.setWinner(winner);
        rewardRepository.save(availableReward);

        // Create an artefact in the winner's inventory
        Artefact artefact = new Artefact();
        artefact.setName(availableReward.getName());
        artefact.setImageUrl(availableReward.getImageUrl());
        artefact.setRarity(mapDifficultyToRarity(hunt.getDifficulty()));
        artefact.setOwner(winner);
        artefactRepository.save(artefact);
    }

    /**
     * Map hunt difficulty to artefact rarity
     */
    private String mapDifficultyToRarity(String difficulty) {
        if (difficulty == null) return "common";
        return switch (difficulty.toLowerCase()) {
            case "easy" -> "common";
            case "medium" -> "rare";
            case "hard" -> "epic";
            case "expert" -> "legendary";
            default -> "common";
        };
    }

    private ParticipationResponse toResponse(Participation participation) {
        Hunt hunt = participation.getHunt();

        List<ParticipationResponse.StepInfo> steps = hunt.getSteps() != null
                ? hunt.getSteps().stream()
                    .map(s -> ParticipationResponse.StepInfo.builder()
                            .id(s.getId())
                            .orderIndex(s.getOrderIndex())
                            .title(s.getTitle())
                            .description(s.getDescription())
                            .latitude(s.getLatitude())
                            .longitude(s.getLongitude())
                            .radius(s.getRadius())
                            .build())
                    .collect(Collectors.toList())
                : List.of();

        List<ParticipationResponse.RewardInfo> rewards = hunt.getRewards() != null
                ? hunt.getRewards().stream()
                    .map(r -> ParticipationResponse.RewardInfo.builder()
                            .id(r.getId())
                            .name(r.getName())
                            .imageUrl(r.getImageUrl())
                            .winnerId(r.getWinner() != null ? r.getWinner().getId() : null)
                            .build())
                    .collect(Collectors.toList())
                : List.of();

        return ParticipationResponse.builder()
                .id(participation.getId())
                .huntId(hunt.getId())
                .huntTitle(hunt.getTitle())
                .huntDifficulty(hunt.getDifficulty())
                .huntDuration(hunt.getDuration())
                .huntReward(hunt.getRewards() != null ? hunt.getRewards().size() : 0)
                .creatorName(hunt.getCreator().getNom())
                .currentStepIndex(participation.getCurrentStepIndex())
                .status(participation.getStatus().name())
                .startedAt(participation.getCreatedAt())
                .completedAt(participation.getStatus() == ParticipationStatus.TERMINE
                        ? LocalDateTime.now() : null)
                .steps(steps)
                .rewards(rewards)
                .build();
    }
}
