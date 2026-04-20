package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.controller.dto.NotificationResponse;
import com.supdevinci.lootopia.model.Hunt;
import com.supdevinci.lootopia.model.Notification;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void createHuntCompletedNotification(Hunt hunt, User organizer) {
        Notification n = new Notification();
        n.setRecipient(organizer);
        n.setMessage("Un joueur a terminé votre chasse : " + hunt.getTitle());
        n.setType("HUNT_COMPLETED");
        n.setHunt(hunt);
        notificationRepository.save(n);
    }

    public List<NotificationResponse> getNotifications(User user) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void markRead(Long id, User user) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getRecipient().getId().equals(user.getId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setMessage(n.getMessage());
        r.setType(n.getType());
        r.setRead(n.isRead());
        if (n.getHunt() != null) {
            r.setHuntId(n.getHunt().getId());
            r.setHuntTitle(n.getHunt().getTitle());
        }
        r.setCreatedAt(n.getCreatedAt().toString());
        return r;
    }
}
