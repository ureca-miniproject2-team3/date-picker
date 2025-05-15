package com.mycom.myapp.notification.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.notifications.controller.NotificationController;
import com.mycom.myapp.notifications.dto.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class NotificationControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void receiveAlert_정상_동작() throws Exception {
        // given
        NotificationDto notificationDto = NotificationDto.builder()
                .userId(1L)
                .eventId(100L)
                .title("테스트 이벤트")
                .build();

        doNothing().when(eventDao).markAsSent(notificationDto.getUserId(), notificationDto.getEventId());

        // when & then
        mockMvc.perform(post("/api/notifications/received")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDto)))
                .andExpect(status().isOk());

        verify(eventDao).markAsSent(notificationDto.getUserId(), notificationDto.getEventId());
    }
}
