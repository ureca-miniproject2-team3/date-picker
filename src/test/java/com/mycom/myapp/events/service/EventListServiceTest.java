package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.dto.EventSummaryDto;
import com.mycom.myapp.events.dto.TimelineDto;
import com.mycom.myapp.events.dto.type.EventStatus;
import com.mycom.myapp.notifications.service.AlertService;
import com.mycom.myapp.schedules.dao.ScheduleDao;

public class EventListServiceTest {

    private EventDao eventDao;
    private ScheduleDao scheduleDao;
    private AlertService alertService;
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventDao = mock(EventDao.class);
        scheduleDao = mock(ScheduleDao.class);
        alertService = mock(AlertService.class);
        eventService = new EventServiceImpl(eventDao, scheduleDao, alertService);
    }

    @Test
    void listEvent_정상_동작() {
        // given
        Long userId = 1L;
        List<EventSummaryDto> eventList = new ArrayList<>();

        TimelineDto timeline = TimelineDto.builder()
        		.timelineId(100L)
        		.eventId(2L)
        		.startTime(LocalDateTime.of(2025, 5, 1, 13, 00, 00))
        		.endTime(LocalDateTime.of(2025, 5, 1, 16, 00, 00))
        		.build();

        EventSummaryDto event1 = new EventSummaryDto();
        event1.setEventId(1L);
        event1.setTitle("테스트 이벤트1");
        event1.setStatus(EventStatus.UNCHECKED);

        EventSummaryDto event2 = new EventSummaryDto();
        event2.setEventId(2L);
        event2.setTitle("테스트 이벤트2");
        event2.setStatus(EventStatus.CHECKED);
        event2.setTimeline(timeline);

        eventList.add(event1);
        eventList.add(event2);

        when(eventDao.listEvent(userId)).thenReturn(eventList);

        // when
        EventResultDto result = eventService.listEvent(userId);

        // then
        assertEquals("success", result.getResult());
        assertNotNull(result.getEventDtoList());
        assertEquals(2, result.getEventDtoList().size());
        assertSame(eventList, result.getEventDtoList());
        verify(eventDao).listEvent(userId);
    }

    @Test
    void listEvent_빈_리스트_반환() {
        // given
        Long userId = 1L;
        List<EventSummaryDto> emptyList = new ArrayList<>();
        when(eventDao.listEvent(userId)).thenReturn(emptyList);

        // when
        EventResultDto result = eventService.listEvent(userId);

        // then
        assertEquals("success", result.getResult());
        assertNotNull(result.getEventDtoList());
        assertEquals(0, result.getEventDtoList().size());
        assertSame(emptyList, result.getEventDtoList());
        verify(eventDao).listEvent(userId);
    }

    @Test
    void listEvent_예외_발생() {
        // given
        Long userId = 1L;
        RuntimeException exception = new RuntimeException("Database error");
        when(eventDao.listEvent(userId)).thenThrow(exception);

        // when
        EventResultDto result = eventService.listEvent(userId);

        // then
        assertEquals("fail", result.getResult());
        verify(eventDao).listEvent(userId);
    }
}
