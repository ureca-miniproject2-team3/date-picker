package com.mycom.myapp.events.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.dto.TimelineDto;
import com.mycom.myapp.events.service.EventService;

public class EventCheckControllerTest {

	private EventService eventService;
	private EventController controller;
	
	@BeforeEach
	void setUp() {
		eventService = mock(EventService.class);
		controller = new EventController(eventService);
	}
	
	@Test
	void checkEvent_success_OK_반환() {
		// given
		Long eventId = 1L;
		Long userId = 1L;
		
		TimelineDto timelineDto = TimelineDto.builder()
				.eventId(eventId)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		EventResultDto resultDto = new EventResultDto();
		resultDto.setResult("success");
		
		// when
		when(eventService.checkEvent(userId, timelineDto)).thenReturn(resultDto);
		ResponseEntity<EventResultDto> response = controller.checkEvent(userId, timelineDto);
		
		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(resultDto, response.getBody());
		verify(eventService).checkEvent(userId, timelineDto);
	}
	
	@Test
	void checkEvent_forbidden_FORBIDDEN_반환() {
		// given
		Long eventId = 1L;
		Long userId = 2L;
		
		TimelineDto timelineDto = TimelineDto.builder()
				.eventId(eventId)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		EventResultDto resultDto = new EventResultDto();
		resultDto.setResult("forbidden");
		
		// when
		when(eventService.checkEvent(userId, timelineDto)).thenReturn(resultDto);
		ResponseEntity<EventResultDto> response = controller.checkEvent(userId, timelineDto);
		
		// then
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertSame(resultDto, response.getBody());
		verify(eventService).checkEvent(userId, timelineDto);
	}
	
	@Test
	void checkEvent_fail_INTERNAL_SERVER_ERROR_반환() {
		// given
		Long eventId = 1L;
		Long userId = 1L;
		
		TimelineDto timelineDto = TimelineDto.builder()
				.eventId(eventId)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		EventResultDto resultDto = new EventResultDto();
		resultDto.setResult("fail");
		
		// when
		when(eventService.checkEvent(userId, timelineDto)).thenReturn(resultDto);
		ResponseEntity<EventResultDto> response = controller.checkEvent(userId, timelineDto);
		
		// then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertSame(resultDto, response.getBody());
		verify(eventService).checkEvent(userId, timelineDto);
	}
}
