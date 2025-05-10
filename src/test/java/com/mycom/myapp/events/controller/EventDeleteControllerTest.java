package com.mycom.myapp.events.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EventDeleteControllerTest {

    private EventService eventService;
    private EventController controller;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventController(eventService);
    }

    @Test
    void deleteEvent_forbidden_FORBIDDEN_반환() {
        // given
        Long eventId = 1L;
        Long userId = 2L; // 이벤트 소유자가 아님
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("forbidden");

        when(eventService.deleteEvent(eventId, userId)).thenReturn(resultDto);

        // when
        ResponseEntity<EventResultDto> response = controller.deleteEvent(eventId, userId);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).deleteEvent(eventId, userId);
    }

    @Test
    void deleteEvent_success_OK_반환() {
        // given
        Long eventId = 1L;
        Long userId = 1L;
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("success");

        when(eventService.deleteEvent(eventId, userId)).thenReturn(resultDto);

        // when
        ResponseEntity<EventResultDto> response = controller.deleteEvent(eventId, userId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).deleteEvent(eventId, userId);
    }

    @Test
    void deleteEvent_fail_INTERNAL_SERVER_ERROR_반환() {
        // given
        Long eventId = 1L;
        Long userId = 1L;
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("fail");

        when(eventService.deleteEvent(eventId, userId)).thenReturn(resultDto);

        // when
        ResponseEntity<EventResultDto> response = controller.deleteEvent(eventId, userId);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).deleteEvent(eventId, userId);
    }
}
