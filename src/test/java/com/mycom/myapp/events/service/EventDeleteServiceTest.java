package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.notifications.service.AlertService;
import com.mycom.myapp.schedules.dao.ScheduleDao;
import com.mycom.myapp.schedules.dto.ScheduleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDeleteServiceTest {

    @Mock
    private EventDao eventDao;

    @Mock
    private ScheduleDao scheduleDao;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventServiceImpl(eventDao, scheduleDao, alertService);
    }

    @Test
    void 이벤트_삭제_권한_없음() {
        // given
        Long eventId = 1L;
        Long userId = 2L; // 이벤트 소유자가 아님
        Long ownerId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(ownerId)
                .build();

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("forbidden", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(scheduleDao, never()).listSchedule(anyLong());
        verify(scheduleDao, never()).deleteSchedule(anyLong());
        verify(eventDao, never()).deleteUserEvent(anyLong());
        verify(eventDao, never()).deleteEventDate(anyLong());
        verify(eventDao, never()).deleteTimeline(anyLong());
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void 이벤트_정상_삭제() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        // 이벤트에 연관된 스케줄 목록 생성
        List<ScheduleDto> schedules = new ArrayList<>();
        ScheduleDto schedule1 = ScheduleDto.builder()
                .scheduleId(101L)
                .eventId(eventId)
                .userId(userId)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();
        ScheduleDto schedule2 = ScheduleDto.builder()
                .scheduleId(102L)
                .eventId(eventId)
                .userId(userId)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();
        schedules.add(schedule1);
        schedules.add(schedule2);

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(scheduleDao.listSchedule(eventId)).thenReturn(schedules);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        verify(eventDao).detailEvent(eventId);
        verify(scheduleDao).listSchedule(eventId);
        verify(scheduleDao).deleteSchedule(101L);
        verify(scheduleDao).deleteSchedule(102L);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao).deleteTimeline(eventId);
        verify(eventDao).deleteEvent(eventId);

        assertEquals("success", result.getResult());
    }

    @Test
    void deleteUserEvent_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        // 이벤트에 연관된 스케줄 목록 생성
        List<ScheduleDto> schedules = new ArrayList<>();
        ScheduleDto schedule1 = ScheduleDto.builder()
                .scheduleId(101L)
                .eventId(eventId)
                .userId(userId)
                .build();
        schedules.add(schedule1);

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(scheduleDao.listSchedule(eventId)).thenReturn(schedules);

        doThrow(new RuntimeException("deleteUserEvent error"))
            .when(eventDao).deleteUserEvent(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(scheduleDao).listSchedule(eventId);
        verify(scheduleDao).deleteSchedule(101L);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao, never()).deleteEventDate(anyLong());
        verify(eventDao, never()).deleteTimeline(anyLong());
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteEventDate_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        // 이벤트에 연관된 스케줄 목록 생성
        List<ScheduleDto> schedules = new ArrayList<>();
        ScheduleDto schedule1 = ScheduleDto.builder()
                .scheduleId(101L)
                .eventId(eventId)
                .userId(userId)
                .build();
        schedules.add(schedule1);

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(scheduleDao.listSchedule(eventId)).thenReturn(schedules);

        doThrow(new RuntimeException("deleteEventDate error"))
            .when(eventDao).deleteEventDate(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(scheduleDao).listSchedule(eventId);
        verify(scheduleDao).deleteSchedule(101L);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao, never()).deleteTimeline(eventId);
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteTimeline_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        // 이벤트에 연관된 스케줄 목록 생성
        List<ScheduleDto> schedules = new ArrayList<>();
        ScheduleDto schedule1 = ScheduleDto.builder()
                .scheduleId(101L)
                .eventId(eventId)
                .userId(userId)
                .build();
        schedules.add(schedule1);

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(scheduleDao.listSchedule(eventId)).thenReturn(schedules);

        doThrow(new RuntimeException("deleteEventDate error"))
            .when(eventDao).deleteTimeline(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(scheduleDao).listSchedule(eventId);
        verify(scheduleDao).deleteSchedule(101L);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao).deleteTimeline(eventId);
        verify(eventDao, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteEvent_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        // 이벤트에 연관된 스케줄 목록 생성
        List<ScheduleDto> schedules = new ArrayList<>();
        ScheduleDto schedule1 = ScheduleDto.builder()
                .scheduleId(101L)
                .eventId(eventId)
                .userId(userId)
                .build();
        schedules.add(schedule1);

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(scheduleDao.listSchedule(eventId)).thenReturn(schedules);

        doThrow(new RuntimeException("deleteEvent error"))
            .when(eventDao).deleteEvent(eventId);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(scheduleDao).listSchedule(eventId);
        verify(scheduleDao).deleteSchedule(101L);
        verify(eventDao).deleteUserEvent(eventId);
        verify(eventDao).deleteEventDate(eventId);
        verify(eventDao).deleteEvent(eventId);
    }

    @Test
    void deleteSchedule_도중_예외_발생() {
        // given
        Long eventId = 1L;
        Long userId = 1L;

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(userId)
                .build();

        // 이벤트에 연관된 스케줄 목록 생성
        List<ScheduleDto> schedules = new ArrayList<>();
        ScheduleDto schedule1 = ScheduleDto.builder()
                .scheduleId(101L)
                .eventId(eventId)
                .userId(userId)
                .build();
        ScheduleDto schedule2 = ScheduleDto.builder()
                .scheduleId(102L)
                .eventId(eventId)
                .userId(userId)
                .build();
        schedules.add(schedule1);
        schedules.add(schedule2);

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);
        when(scheduleDao.listSchedule(eventId)).thenReturn(schedules);

        // 첫 번째 스케줄 삭제 시 예외 발생
        doThrow(new RuntimeException("deleteSchedule error"))
            .when(scheduleDao).deleteSchedule(101L);

        // when
        EventResultDto result = eventService.deleteEvent(eventId, userId);

        // then
        assertEquals("fail", result.getResult());

        verify(eventDao).detailEvent(eventId);
        verify(scheduleDao).listSchedule(eventId);
        verify(scheduleDao).deleteSchedule(101L);
        verify(scheduleDao, never()).deleteSchedule(102L);
        verify(eventDao, never()).deleteUserEvent(anyLong());
        verify(eventDao, never()).deleteEventDate(anyLong());
        verify(eventDao, never()).deleteTimeline(anyLong());
        verify(eventDao, never()).deleteEvent(anyLong());
    }
}
