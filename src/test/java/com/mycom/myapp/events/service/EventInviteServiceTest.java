package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventInviteServiceTest {
    private EventDao eventDao;
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventDao = mock(EventDao.class);
        eventService = new EventServiceImpl(eventDao);
    }

    @Test
    void 이벤트_초대_정상_동작() {
        // given
        Long inviterId = 1L;
        Long eventId = 100L;
        List<Long> invitedIds = Arrays.asList(2L, 3L);
        List<Long> existingParticipants = Arrays.asList(1L);
        
        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(inviterId)
                .build();
        
        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(eventDao.getParticipantsByEventId(eventId)).thenReturn(existingParticipants);

        // when
        EventResultDto result = eventService.inviteUserToEvent(inviterId, eventId, invitedIds);

        // then
        assertEquals("success", result.getResult());
        verify(eventDao).detailEvent(eventId);
        verify(eventDao).getParticipantsByEventId(eventId);
        verify(eventDao).insertUserEvent(2L, eventId);
        verify(eventDao).insertUserEvent(3L, eventId);
    }

    @Test
    void 이벤트_초대_권한_없음() {
        // given
        Long inviterId = 2L; // 이벤트 소유자가 아님
        Long ownerId = 1L;
        Long eventId = 100L;
        List<Long> invitedIds = Arrays.asList(3L, 4L);
        
        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(ownerId)
                .build();
        
        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        // when
        EventResultDto result = eventService.inviteUserToEvent(inviterId, eventId, invitedIds);

        // then
        assertEquals("forbidden", result.getResult());
        verify(eventDao).detailEvent(eventId);
        verify(eventDao, never()).getParticipantsByEventId(anyLong());
        verify(eventDao, never()).insertUserEvent(anyLong(), anyLong());
    }

    @Test
    void 이벤트_초대_이미_참가자인_경우() {
        // given
        Long inviterId = 1L;
        Long eventId = 100L;
        List<Long> invitedIds = Arrays.asList(2L, 3L);
        List<Long> existingParticipants = Arrays.asList(1L, 2L); // 2L은 이미 참가자
        
        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(inviterId)
                .build();
        
        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(eventDao.getParticipantsByEventId(eventId)).thenReturn(existingParticipants);

        // when
        EventResultDto result = eventService.inviteUserToEvent(inviterId, eventId, invitedIds);

        // then
        assertEquals("success", result.getResult());
        verify(eventDao).detailEvent(eventId);
        verify(eventDao).getParticipantsByEventId(eventId);
        verify(eventDao, never()).insertUserEvent(2L, eventId); // 이미 참가자이므로 호출되지 않아야 함
        verify(eventDao).insertUserEvent(3L, eventId);
    }

    @Test
    void 이벤트_초대_예외_발생() {
        // given
        Long inviterId = 1L;
        Long eventId = 100L;
        List<Long> invitedIds = Arrays.asList(2L, 3L);
        
        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(inviterId)
                .build();
        
        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(eventDao.getParticipantsByEventId(eventId)).thenThrow(new RuntimeException("데이터베이스 오류"));

        // when
        EventResultDto result = eventService.inviteUserToEvent(inviterId, eventId, invitedIds);

        // then
        assertEquals("fail", result.getResult());
        verify(eventDao).detailEvent(eventId);
        verify(eventDao).getParticipantsByEventId(eventId);
        verify(eventDao, never()).insertUserEvent(anyLong(), anyLong());
    }
}