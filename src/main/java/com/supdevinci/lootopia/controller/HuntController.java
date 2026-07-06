package com.supdevinci.lootopia.controller;

import com.supdevinci.lootopia.dto.HuntSummaryDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hunts")
public class HuntController {

    @GetMapping
    public List<HuntSummaryDto> getPublishedHunts() {
        return List.of();
    }

    @GetMapping("/mine")
    public List<HuntSummaryDto> getMyHunts() {
        return List.of();
    }
}
