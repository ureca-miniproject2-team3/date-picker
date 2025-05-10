package com.mycom.myapp.events.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.service.EventService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EventInviteControllerTest {

    private EventService eventService;
    private EventController controller;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventController(eventService);
    }

    @Test
    void inviteUserToEvent_success_OK_반환() {
        // given
        Long inviterId = 1L;
        Long eventId = 100L;
        List<Long> invitedIds = Arrays.asList(2L, 3L);
        
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("success");

        when(eventService.inviteUserToEvent(inviterId, eventId, invitedIds)).thenReturn(resultDto);

        // when
        ResponseEntity<EventResultDto> response = controller.inviteUserToEvent(inviterId, eventId, invitedIds);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).inviteUserToEvent(inviterId, eventId, invitedIds);
    }

    @Test
    void inviteUserToEvent_forbidden_FORBIDDEN_반환() {
        // given
        Long inviterId = 2L; // 이벤트 소유자가 아님
        Long eventId = 100L;
        List<Long> invitedIds = Arrays.asList(3L, 4L);
        
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("forbidden");

        when(eventService.inviteUserToEvent(inviterId, eventId, invitedIds)).thenReturn(resultDto);

        // when
        ResponseEntity<EventResultDto> response = controller.inviteUserToEvent(inviterId, eventId, invitedIds);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).inviteUserToEvent(inviterId, eventId, invitedIds);
    }

    @Test
    void inviteUserToEvent_fail_INTERNAL_SERVER_ERROR_반환() {
        // given
        Long inviterId = 1L;
        Long eventId = 100L;
        List<Long> invitedIds = Arrays.asList(2L, 3L);
        
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("fail");

        when(eventService.inviteUserToEvent(inviterId, eventId, invitedIds)).thenReturn(resultDto);

        // when
        ResponseEntity<EventResultDto> response = controller.inviteUserToEvent(inviterId, eventId, invitedIds);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).inviteUserToEvent(inviterId, eventId, invitedIds);
    }
}