package com.mycom.myapp.notifications.service;

import com.mycom.myapp.notifications.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService{

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotifications(Long userId, Long eventId, String title) {
        NotificationDto notificationDto = new NotificationDto(userId, eventId, title);
        messagingTemplate.convertAndSend("/topic/user/" + userId, notificationDto);
    }

    @Override
    public void sendUnsentNotifications(Long userId) {

    }
}
