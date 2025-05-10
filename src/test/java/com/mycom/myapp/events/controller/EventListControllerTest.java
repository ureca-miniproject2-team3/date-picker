package com.mycom.myapp.events.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.dto.EventSummaryDto;
import com.mycom.myapp.events.service.EventService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EventListControllerTest {

    private EventService eventService;
    private EventController controller;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventController(eventService);
    }

    @Test
    void listEvent_success_OK_반환() {
        // given
        Long userId = 1L;
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("success");
        
        List<EventSummaryDto> eventList = new ArrayList<>();
        EventSummaryDto event1 = new EventSummaryDto();
        event1.setEventId(1L);
        event1.setTitle("테스트 이벤트1");
        eventList.add(event1);
        
        resultDto.setEventDtoList(eventList);

        // when
        when(eventService.listEvent(userId)).thenReturn(resultDto);
        ResponseEntity<EventResultDto> response = controller.listEvent(userId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).listEvent(userId);
    }

    @Test
    void listEvent_fail_INTERNAL_SERVER_ERROR_반환() {
        // given
        Long userId = 1L;
        EventResultDto resultDto = new EventResultDto();
        resultDto.setResult("fail");

        // when
        when(eventService.listEvent(userId)).thenReturn(resultDto);
        ResponseEntity<EventResultDto> response = controller.listEvent(userId);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertSame(resultDto, response.getBody());
        verify(eventService).listEvent(userId);
    }
}