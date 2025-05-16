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

public class EventCreateControllerTest {

    private EventService eventService;
    private EventController controller;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventController(eventService);
    }

    @Test
    void createEvent_success_OK_반환() {
        // given
        EventDto dto = EventDto.builder().build();
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("success");
        resultDto.setEventDto(dto);

        // when
        when(eventService.createEvent(dto)).thenReturn(resultDto);
        ResponseEntity<EventResultDto> response = controller.createEvent(dto);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).createEvent(dto);
    }

    @Test
    void createEvent_fail_INTERNAL_SERVER_ERROR_반환() {
        // given
        EventDto dto = EventDto.builder().build();
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("fail");

        // when
        when(eventService.createEvent(dto)).thenReturn(resultDto);
        ResponseEntity<EventResultDto> response = controller.createEvent(dto);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).createEvent(dto);
    }
}
