package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EventDeleteServiceTest {

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 이벤트_삭제_권한_없음() {
        // given
        Long eventId = 1L;
        Long userId = 2L; // 이벤트 소유자가 아님
        Long ownerId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(ownerId)
                .build();

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("forbidden", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(eventDao, never()).deleteUserEvent(anyLong());
        verify(eventDao, never()).deleteEventDate(anyLong());
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void 이벤트_정상_삭제() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        verify(eventDao).detailEvent(eventId);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao).deleteEvent(eventId);

        assertEquals("success", result.getResult());
    }

    @Test
    void deleteUserEvent_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        doThrow(new RuntimeException("deleteUserEvent error"))
            .when(eventDao).deleteUserEvent(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao, never()).deleteEventDate(anyLong());
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteEventDate_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        doThrow(new RuntimeException("deleteEventDate error"))
            .when(eventDao).deleteEventDate(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteEvent_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        doThrow(new RuntimeException("deleteEvent error"))
            .when(eventDao).deleteEvent(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao).deleteEvent(eventId);
    }
}
