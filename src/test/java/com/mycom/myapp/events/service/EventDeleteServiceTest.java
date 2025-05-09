package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.mycom.myapp.events.dao.EventDao;
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
    void 이벤트_정상_삭제() {
        // given
        Long eventId = 1L;

        // when
        EventResultDto result = eventService.deleteEvent(eventId);

        // then
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao).deleteEvent(eventId);

        assertEquals("success", result.getResult());
    }

    @Test
    void deleteUserEvent_도중_예외_발생() {
        // given
        Long eventId = 1L;
        
        doThrow(new RuntimeException("deleteUserEvent error"))
            .when(eventDao).deleteUserEvent(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId);

        // then
        assertEquals("fail", result.getResult());
        
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao, never()).deleteEventDate(anyLong());
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteEventDate_도중_예외_발생() {
        // given
        Long eventId = 1L;
        
        doThrow(new RuntimeException("deleteEventDate error"))
            .when(eventDao).deleteEventDate(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId);

        // then
        assertEquals("fail", result.getResult());
        
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteEvent_도중_예외_발생() {
        // given
        Long eventId = 1L;
        
        doThrow(new RuntimeException("deleteEvent error"))
            .when(eventDao).deleteEvent(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId);

        // then
        assertEquals("fail", result.getResult());
        
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao).deleteEvent(eventId);
    }
}