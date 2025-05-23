package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.notifications.service.AlertService;
import com.mycom.myapp.schedules.dao.ScheduleDao;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventCreateServiceTest {
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
    void 이벤트_정상_생성() {
        // given
        LocalDate d1 = LocalDate.of(2025, 5, 1);
        LocalDate d2 = LocalDate.of(2025, 5, 2);
        List<LocalDate> dates = Arrays.asList(d1, d2);

        EventDto dto = EventDto.builder()
                .eventId(1L)
                .ownerId(42L)
                .eventDates(dates)
                .build();

        doNothing().when(eventDao).insertEvent(dto);
        doNothing().when(eventDao).insertEventDate(anyLong(), any());
        doNothing().when(eventDao).insertUserEvent(anyLong(), anyLong());

        // when
        EventResultDto result = eventService.createEvent(dto);

        // then
        assertEquals("success", result.getResult());
        assertEquals(dto, result.getEventDto());

        verify(eventDao).insertEvent(dto);
        verify(eventDao).insertEventDate(1L, d1);
        verify(eventDao).insertEventDate(1L, d2);
        verify(eventDao).insertUserEvent(42L, 1L);
    }

    @Test
    void insertEvent_도중_예외_발생() {
        // given
        LocalDate day = LocalDate.of(2025, 5, 1);
        EventDto dto = EventDto.builder()
                .eventId(5L)
                .ownerId(7L)
                .eventDates(List.of(day))
                .build();

        RuntimeException ex1 = new RuntimeException("insertEvent error");
        doThrow(ex1).when(eventDao).insertEvent(dto);

        // when
        EventResultDto result = eventService.createEvent(dto);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).insertEvent(dto);
        verify(eventDao, never()).insertEventDate(anyLong(), any());
        verify(eventDao, never()).insertUserEvent(anyLong(), anyLong());
    }

    @Test
    void insertEventDate_도중_예외_발생() {
        // given
        LocalDate d1 = LocalDate.of(2025, 5, 1);
        LocalDate d2 = LocalDate.of(2025, 5, 2);
        List<LocalDate> dates = Arrays.asList(d1, d2);

        EventDto dto = EventDto.builder()
                .eventId(8L)
                .ownerId(9L)
                .eventDates(dates)
                .build();

        doNothing().when(eventDao).insertEvent(dto);
        doNothing().when(eventDao).insertEventDate(8L, d1);
        RuntimeException ex2 = new RuntimeException("insertEventDate error");
        doThrow(ex2).when(eventDao).insertEventDate(8L, d2);

        // when
        EventResultDto result = eventService.createEvent(dto);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).insertEvent(dto);
        verify(eventDao).insertEventDate(8L, d1);
        verify(eventDao).insertEventDate(8L, d2);
        verify(eventDao, never()).insertUserEvent(anyLong(), anyLong());
    }

    @Test
    void insertUserEvent_도중_예외_발생() {
        // given
        LocalDate d1 = LocalDate.of(2025, 5, 1);
        EventDto dto = EventDto.builder()
                .eventId(13L)
                .ownerId(21L)
                .eventDates(List.of(d1))
                .build();

        doNothing().when(eventDao).insertEvent(dto);
        doNothing().when(eventDao).insertEventDate(13L, d1);
        RuntimeException ex3 = new RuntimeException("insertUserEvent error");
        doThrow(ex3).when(eventDao).insertUserEvent(21L, 13L);

        // when
        EventResultDto result = eventService.createEvent(dto);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).insertEvent(dto);
        verify(eventDao).insertEventDate(13L, d1);
        verify(eventDao).insertUserEvent(21L, 13L);
    }
}
