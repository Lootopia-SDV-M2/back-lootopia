package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.dto.ArtefactDto;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.ArtefactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtefactService {

    private final ArtefactRepository artefactRepository;

    @Transactional(readOnly = true)
    public List<ArtefactDto> getInventoryFor(User owner) {
        return artefactRepository.findByOwner(owner)
                .stream()
                .map(ArtefactDto::from)
                .toList();
    }
}
