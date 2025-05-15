package com.mycom.myapp.notifications.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.notifications.dto.NotificationDto;
import com.mycom.myapp.notifications.dto.UserEventDto;

public class SendUnsentNotificationsTest {

    @Mock
    private EventDao eventDao;
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    
    @InjectMocks
    private AlertServiceImpl alertService;
    
    private final Long TEST_USER_ID = 123L;
    private final Long TEST_EVENT_ID = 456L;
    private final Long TEST_OWNER_ID = 789L;
    private final String TEST_EVENT_TITLE = "테스트 이벤트";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void 미전송_알림이_없을때() {
        // given
        when(eventDao.listUnsentUserEvent(TEST_USER_ID)).thenReturn(Collections.emptyList());
        
        // when
        alertService.sendUnsentNotifications(TEST_USER_ID);
        
        // then
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(NotificationDto.class));
        verify(eventDao, never()).updateUnsentUserEvent(anyLong(), anyLong());
    }
    
    @Test
    void 미전송_알림_정상_전송() {
        // given
        UserEventDto userEvent1 = new UserEventDto();
        userEvent1.setUserId(TEST_USER_ID);
        userEvent1.setEventId(TEST_EVENT_ID);
        
        UserEventDto userEvent2 = new UserEventDto();
        userEvent2.setUserId(TEST_USER_ID);
        userEvent2.setEventId(TEST_EVENT_ID + 1);
        
        List<UserEventDto> userEvents = Arrays.asList(userEvent1, userEvent2);
        
        EventDto eventDto1 = new EventDto();
        eventDto1.setEventId(TEST_EVENT_ID);
        eventDto1.setOwnerId(TEST_OWNER_ID); // 이벤트 소유자는 다른 사용자
        eventDto1.setTitle(TEST_EVENT_TITLE);
        
        EventDto eventDto2 = new EventDto();
        eventDto2.setEventId(TEST_EVENT_ID + 1);
        eventDto2.setOwnerId(TEST_OWNER_ID);
        eventDto2.setTitle(TEST_EVENT_TITLE + "2");
        
        when(eventDao.listUnsentUserEvent(TEST_USER_ID)).thenReturn(userEvents);
        when(eventDao.detailEvent(TEST_EVENT_ID)).thenReturn(eventDto1);
        when(eventDao.detailEvent(TEST_EVENT_ID + 1)).thenReturn(eventDto2);
        
        // when
        alertService.sendUnsentNotifications(TEST_USER_ID);
        
        // then
        verify(messagingTemplate, times(2)).convertAndSend(eq("/topic/user/" + TEST_USER_ID), 
                any(NotificationDto.class));
        
        verify(eventDao).updateUnsentUserEvent(TEST_USER_ID, TEST_EVENT_ID);
        verify(eventDao).updateUnsentUserEvent(TEST_USER_ID, TEST_EVENT_ID + 1);
    }
    
    @Test
    void 이벤트_소유자에게는_알림_전송_안함() {
        // given
        UserEventDto userEvent = new UserEventDto();
        userEvent.setUserId(TEST_USER_ID);
        userEvent.setEventId(TEST_EVENT_ID);
        
        List<UserEventDto> userEvents = Collections.singletonList(userEvent);
        
        EventDto eventDto = new EventDto();
        eventDto.setEventId(TEST_EVENT_ID);
        eventDto.setOwnerId(TEST_USER_ID); // 이벤트 소유자와 현재 사용자가 동일
        eventDto.setTitle(TEST_EVENT_TITLE);
        
        when(eventDao.listUnsentUserEvent(TEST_USER_ID)).thenReturn(userEvents);
        when(eventDao.detailEvent(TEST_EVENT_ID)).thenReturn(eventDto);
        
        alertService.sendUnsentNotifications(TEST_USER_ID);
        
        // then
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(NotificationDto.class));
        verify(eventDao, never()).updateUnsentUserEvent(anyLong(), anyLong());
    }
    
    @Test
    void 알림_전송_중_예외_발생시_처리() {
        // given
        UserEventDto userEvent = new UserEventDto();
        userEvent.setUserId(TEST_USER_ID);
        userEvent.setEventId(TEST_EVENT_ID);
        
        List<UserEventDto> userEvents = Collections.singletonList(userEvent);
        
        EventDto eventDto = new EventDto();
        eventDto.setEventId(TEST_EVENT_ID);
        eventDto.setOwnerId(TEST_OWNER_ID);
        eventDto.setTitle(TEST_EVENT_TITLE);
        
        when(eventDao.listUnsentUserEvent(TEST_USER_ID)).thenReturn(userEvents);
        when(eventDao.detailEvent(TEST_EVENT_ID)).thenReturn(eventDto);
        doThrow(new RuntimeException("WebSocket 전송 오류"))
        .when(messagingTemplate).convertAndSend(anyString(), any(NotificationDto.class));

        
        try {
            alertService.sendUnsentNotifications(TEST_USER_ID);
            org.junit.jupiter.api.Assertions.fail("예외가 발생해야 합니다");
        } catch (RuntimeException e) {
            verify(eventDao, never()).updateUnsentUserEvent(anyLong(), anyLong());
        }
    }
    
}