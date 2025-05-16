package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.notifications.service.AlertService;
import com.mycom.myapp.schedules.dao.ScheduleDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventStatusServiceTest {
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
    void 이벤트_상태_업데이트_정상_동작() {
        // given
        doNothing().when(eventDao).completedCheckedEvents();
        doNothing().when(eventDao).expiredUncheckedEvents();

        // when
        EventResultDto result = eventService.updateEventStatus();

        // then
        assertEquals("success", result.getResult());
        verify(eventDao).completedCheckedEvents();
        verify(eventDao).expiredUncheckedEvents();
    }

    @Test
    void completedCheckedEvents_예외_발생() {
        // given
        RuntimeException exception = new RuntimeException("completedCheckedEvents error");
        doThrow(exception).when(eventDao).completedCheckedEvents();

        // when
        EventResultDto result = eventService.updateEventStatus();

        // then
        assertEquals("fail", result.getResult());
        verify(eventDao).completedCheckedEvents();
    }

    @Test
    void expiredUncheckedEvents_예외_발생() {
        // given
        doNothing().when(eventDao).completedCheckedEvents();
        RuntimeException exception = new RuntimeException("expiredUncheckedEvents error");
        doThrow(exception).when(eventDao).expiredUncheckedEvents();

        // when
        EventResultDto result = eventService.updateEventStatus();

        // then
        assertEquals("fail", result.getResult());
        verify(eventDao).completedCheckedEvents();
        verify(eventDao).expiredUncheckedEvents();
    }
}
