package com.mycom.myapp.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.mycom.myapp.notifications.dto.NotificationDto;
import com.mycom.myapp.notifications.service.AlertServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SendNotificationsTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AlertServiceImpl alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotifications_정상_동작() {
        // given
        Long userId = 1L;
        Long eventId = 100L;
        String title = "테스트 이벤트";

        // when
        alertService.sendNotifications(userId, eventId, title);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/user/" + userId),
                any(NotificationDto.class));
    }

    @Test
    void sendNotifications_사용자_ID_검증() {
        // given
        Long userId = 2L;
        Long eventId = 100L;
        String title = "테스트 이벤트";

        // when
        alertService.sendNotifications(userId, eventId, title);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/user/" + userId),
                any(NotificationDto.class));
    }

    @Test
    void sendNotifications_알림_내용_검증() {
        // given
        Long userId = 1L;
        Long eventId = 100L;
        String title = "테스트 이벤트";
        NotificationDto expectedDto = new NotificationDto(userId, eventId, title);

        // when
        alertService.sendNotifications(userId, eventId, title);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/user/" + userId),
                eq(expectedDto));
    }
}