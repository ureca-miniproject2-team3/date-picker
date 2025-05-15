package com.mycom.myapp.notifications.controller;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.notifications.dto.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EventDao eventDao;

    @PostMapping("/received")
    @Operation(summary = "클라이언트 알림 수신 여부", description = "클라이언트에서 알림을 수신하면 해당하는 userId 와 eventId 를 응답해 알림을 정상적으로 수신했음을 표시합니다.")
    public ResponseEntity<Void> receiveAlert(NotificationDto notificationDto) {
        eventDao.markAsSent(notificationDto.getUserId(), notificationDto.getEventId());
        return ResponseEntity.ok().build();
    }
}
