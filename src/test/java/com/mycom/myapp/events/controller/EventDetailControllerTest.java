package com.mycom.myapp.events.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EventDetailControllerTest {

    private EventService eventService;
    private EventController controller;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventController(eventService);
    }

    @Test
    void detailEvent_success_OK_반환() {
        // given
        Long eventId = 1L;
        EventDto dto = EventDto.builder().eventId(eventId).build();
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("success");
        resultDto.setEventDto(dto);

        // when
        when(eventService.detailEvent(eventId)).thenReturn(resultDto);
        ResponseEntity<EventResultDto> response = controller.detailEvent(eventId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).detailEvent(eventId);
    }

    @Test
    void detailEvent_not_found_BAD_REQUEST_반환() {
        // given
        Long eventId = 999L;
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("not found");

        // when
        when(eventService.detailEvent(eventId)).thenReturn(resultDto);
        ResponseEntity<EventResultDto> response = controller.detailEvent(eventId);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).detailEvent(eventId);
    }

    @Test
    void detailEvent_fail_INTERNAL_SERVER_ERROR_반환() {
        // given
        Long eventId = 1L;
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("fail");

        // when
        when(eventService.detailEvent(eventId)).thenReturn(resultDto);
        ResponseEntity<EventResultDto> response = controller.detailEvent(eventId);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).detailEvent(eventId);
    }
}