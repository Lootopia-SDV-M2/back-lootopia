package com.supdevinci.lootopia.repository;

import com.supdevinci.lootopia.model.Participation;
import com.supdevinci.lootopia.model.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUserId(Long userId);

    List<Participation> findByUserIdAndStatus(Long userId, ParticipationStatus status);

    Optional<Participation> findByUserIdAndHuntId(Long userId, Long huntId);

    boolean existsByUserIdAndHuntIdAndStatus(Long userId, Long huntId, ParticipationStatus status);

    long countByHuntIdAndStatus(Long huntId, ParticipationStatus status);
}
