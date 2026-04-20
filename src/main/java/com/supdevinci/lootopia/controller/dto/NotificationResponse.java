package com.supdevinci.lootopia.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private String type;
    private boolean read;
    private Long huntId;
    private String huntTitle;
    private String createdAt;
}
