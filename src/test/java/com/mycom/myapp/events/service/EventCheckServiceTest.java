package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.dto.TimelineDto;
import com.mycom.myapp.notifications.service.AlertService;
import com.mycom.myapp.schedules.dao.ScheduleDao;

public class EventCheckServiceTest {

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
	}

	@Test
	void 이벤트_상태_확정_정상_동작() {
		// given
		Long eventId = 1L;
		Long userId = 1L;
		Long ownerId = 1L;

		TimelineDto timelineDto = TimelineDto.builder()
					.eventId(eventId)
					.startTime(LocalDateTime.of(2025, 05, 01, 13, 00, 00))
					.endTime(LocalDateTime.of(2025, 05, 01, 16, 00, 00))
					.build();

        EventDto eventDto = EventDto.builder()
                .eventId(eventId)
                .ownerId(ownerId)
                .title("이벤트 테스트")
                .eventDates(List.of(
                        LocalDate.of(2025, 5, 1),  // 기존에 존재하는 날짜
                        LocalDate.of(2025, 5, 2),  // 새로 추가할 날짜
                        LocalDate.of(2025, 5, 3)   // 새로 추가할 날짜
                ))
                .build();

		when(eventDao.detailEvent(eventId)).thenReturn(eventDto);
		doNothing().when(eventDao).checkEvent(timelineDto.getEventId());
		doNothing().when(eventDao).insertTimeline(timelineDto);

		// when
		EventResultDto result = eventService.checkEvent(userId, timelineDto);


		// then
		assertEquals("success", result.getResult());

		verify(eventDao).detailEvent(eventId);
		verify(eventDao).checkEvent(1L);
		verify(eventDao).insertTimeline(timelineDto);
	}

	@Test
	void 이벤트_상태_확정_계정_다름() {
		// given
		Long eventId = 1L;
		Long userId = 1L;
		Long ownerId = 2L;

		TimelineDto timelineDto = TimelineDto.builder()
					.eventId(eventId)
					.startTime(LocalDateTime.of(2025, 05, 01, 13, 00, 00))
					.endTime(LocalDateTime.of(2025, 05, 01, 16, 00, 00))
					.build();

        EventDto eventDto = EventDto.builder()
                .eventId(eventId)
                .ownerId(ownerId)
                .title("이벤트 테스트")
                .eventDates(List.of(
                        LocalDate.of(2025, 5, 1),  // 기존에 존재하는 날짜
                        LocalDate.of(2025, 5, 2),  // 새로 추가할 날짜
                        LocalDate.of(2025, 5, 3)   // 새로 추가할 날짜
                ))
                .build();

		when(eventDao.detailEvent(eventId)).thenReturn(eventDto);
		doNothing().when(eventDao).checkEvent(timelineDto.getEventId());
		doNothing().when(eventDao).insertTimeline(timelineDto);

		// when
		EventResultDto result = eventService.checkEvent(userId, timelineDto);


		// then
		assertEquals("forbidden", result.getResult());

		verify(eventDao).detailEvent(eventId);
	}

	@Test
	void 이벤트_상태_확정_예외_발생() {
		// given
		Long eventId = 1L;
		Long userId = 1L;
		Long ownerId = 2L;

		TimelineDto timelineDto = TimelineDto.builder()
					.eventId(eventId)
					.startTime(LocalDateTime.of(2025, 05, 01, 13, 00, 00))
					.endTime(LocalDateTime.of(2025, 05, 01, 16, 00, 00))
					.build();

        EventDto eventDto = EventDto.builder()
                .eventId(eventId)
                .ownerId(ownerId)
                .title("이벤트 테스트")
                .eventDates(List.of(
                        LocalDate.of(2025, 5, 1),  // 기존에 존재하는 날짜
                        LocalDate.of(2025, 5, 2),  // 새로 추가할 날짜
                        LocalDate.of(2025, 5, 3)   // 새로 추가할 날짜
                ))
                .build();

		when(eventDao.detailEvent(eventId))
				.thenThrow(new RuntimeException("DB Error"));

		// when
		EventResultDto result = eventService.checkEvent(userId, timelineDto);


		// then
		assertEquals("fail", result.getResult());
	}
}
