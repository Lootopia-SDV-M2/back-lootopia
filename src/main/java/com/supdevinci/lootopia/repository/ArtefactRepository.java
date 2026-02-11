package com.supdevinci.lootopia.repository;

import com.supdevinci.lootopia.model.Artefact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtefactRepository extends JpaRepository<Artefact, Long> {
    List<Artefact> findByOwnerId(Long ownerId);
}
