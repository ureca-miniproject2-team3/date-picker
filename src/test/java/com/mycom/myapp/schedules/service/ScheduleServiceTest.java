package com.mycom.myapp.schedules.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycom.myapp.schedules.dao.ScheduleDao;
import com.mycom.myapp.schedules.dto.ScheduleDto;
import com.mycom.myapp.schedules.dto.ScheduleResultDto;
import com.mycom.myapp.schedules.dto.TimeSlotDto;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

	@InjectMocks
	private ScheduleServiceImpl scheduleService;

	@Mock
	private ScheduleDao scheduleDao;

	@Test
	void insertScheduleTest_Success() {
		ScheduleDto scheduleDto = ScheduleDto.builder()	
						.userId(1L)
						.eventId(205L)
						.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
						.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
						.build();

		when(scheduleDao.insertSchedule(scheduleDto)).thenReturn(1);

		ScheduleResultDto result = scheduleService.insertSchedule(scheduleDto);

		assertEquals("success", result.getResult());
	}

	@Test
	void insertScheduleTest_Fail() {
		ScheduleDto scheduleDto = ScheduleDto.builder()	
						.userId(1L)
						.eventId(205L)
						.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
						.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
						.build();

		when(scheduleDao.insertSchedule(scheduleDto)).thenReturn(-1);

		ScheduleResultDto result = scheduleService.insertSchedule(scheduleDto);

		assertEquals("fail", result.getResult());
	}

	@Test
	void insertScheduleTest_DBFail() {
		ScheduleDto scheduleDto = ScheduleDto.builder()	
						.userId(1L)
						.eventId(205L)
						.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
						.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
						.build();

		when(scheduleDao.insertSchedule(scheduleDto))
			.thenThrow(new RuntimeException("DB Error"));

		ScheduleResultDto result = scheduleService.insertSchedule(scheduleDto);

		assertEquals("fail", result.getResult());
	}

	@Test
	void listScheduleTest_Success() {
		ScheduleDto schedule1 = ScheduleDto.builder()
						.scheduleId(1L)
						.userId(1L)
						.eventId(205L)
						.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
						.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
						.build();

		ScheduleDto schedule2 = ScheduleDto.builder()
				.scheduleId(2L)
				.userId(1L)
				.eventId(205L)
				.startTime(LocalDateTime.of(2025, 5, 16, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 16, 15, 00, 00))
				.build();

		when(scheduleDao.listSchedule(any(Long.class))).thenReturn(List.of(schedule1, schedule2));

		ScheduleResultDto result = scheduleService.listSchedule(205L);

		assertEquals("success", result.getResult());
		assertIterableEquals(List.of(schedule1, schedule2), result.getScheduleDtoList());
	}

	@Test
	void listScheduleTest_Fail() {
		when(scheduleDao.listSchedule(any(Long.class)))
				.thenThrow(new RuntimeException("DB Error"));

		ScheduleResultDto result = scheduleService.listSchedule(Long.MAX_VALUE);

		assertEquals("fail", result.getResult());
		assertNull(result.getScheduleDtoList());
	}

	@Test
	void detailScheduleTest_Success() {
		ScheduleDto schedule = ScheduleDto.builder()
						.scheduleId(1L)
						.userId(1L)
						.eventId(205L)
						.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
						.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
						.build();

		when(scheduleDao.detailSchedule(1L)).thenReturn(schedule);

		ScheduleResultDto result = scheduleService.detailSchedule(1L);

		assertEquals("success", result.getResult());
		assertEquals(schedule, result.getScheduleDto());
	}

	@Test
	void detailScheduleTest_Fail() {
		when(scheduleDao.detailSchedule(any(Long.class)))
				.thenThrow(new RuntimeException("DB Error"));

		ScheduleResultDto result = scheduleService.detailSchedule(Long.MAX_VALUE);

		assertEquals("fail", result.getResult());
		assertNull(result.getScheduleDto());
	}

	@Test
	void getMaxOverlapSlotsTest_Success() {
		// given
		Long eventId = 205L;

		// schedule1: 13:00 - 15:00
		ScheduleDto schedule1 = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.eventId(eventId)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 0, 0))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 0, 0))
				.build();

		// schedule2: 14:00 - 16:00
		ScheduleDto schedule2 = ScheduleDto.builder()
				.scheduleId(2L)
				.userId(2L)
				.eventId(eventId)
				.startTime(LocalDateTime.of(2025, 5, 15, 14, 0, 0))
				.endTime(LocalDateTime.of(2025, 5, 15, 16, 0, 0))
				.build();

		// schedule3: 14:30 - 17:00
		ScheduleDto schedule3 = ScheduleDto.builder()
				.scheduleId(3L)
				.userId(3L)
				.eventId(eventId)
				.startTime(LocalDateTime.of(2025, 5, 15, 14, 30, 0))
				.endTime(LocalDateTime.of(2025, 5, 15, 17, 0, 0))
				.build();

		when(scheduleDao.listSchedule(eventId)).thenReturn(List.of(schedule1, schedule2, schedule3));

		// when
		ScheduleResultDto result = scheduleService.getMaxOverlapSlots(eventId);

		// then
		assertEquals("success", result.getResult());
		assertEquals(3, result.getMaxCount()); // Maximum 3 users overlap

		List<TimeSlotDto> expectedTimeSlots = List.of(
				new TimeSlotDto(
						LocalDateTime.of(2025, 5, 15, 14, 30, 0),
						LocalDateTime.of(2025, 5, 15, 15, 0, 0),
						List.of(1L, 2L, 3L)
				)
		);

		assertEquals(1, result.getTimeSlots().size());

		TimeSlotDto actualSlot = result.getTimeSlots().get(0);
		TimeSlotDto expectedSlot = expectedTimeSlots.get(0);

		assertEquals(expectedSlot.getStart(), actualSlot.getStart());
		assertEquals(expectedSlot.getEnd(), actualSlot.getEnd());

		assertEquals(expectedSlot.getUserIds().size(), actualSlot.getUserIds().size());
		assertTrue(actualSlot.getUserIds().containsAll(expectedSlot.getUserIds()));
	}

	@Test
	void getMaxOverlapSlotsTest_Fail() {
		// given
		Long eventId = 205L;

		when(scheduleDao.listSchedule(eventId))
				.thenThrow(new RuntimeException("DB Error"));

		// when
		ScheduleResultDto result = scheduleService.getMaxOverlapSlots(eventId);

		// then
		assertEquals("fail", result.getResult());
		assertNull(result.getTimeSlots());
		assertNull(result.getMaxCount());
	}
	
	@Test
	void updateScheduleTest_Success() {
		ScheduleDto inputSchedule = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		ScheduleDto storedSchedule = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.build();
		
		when(scheduleDao.detailSchedule(1L)).thenReturn(storedSchedule);
		when(scheduleDao.updateSchedule(inputSchedule)).thenReturn(1);
		
		ScheduleResultDto result = scheduleService.updateSchedule(inputSchedule);
		
		assertEquals("success", result.getResult());
	}
	
	@Test
	void updateScheduleTest_Unauthorized() {
		ScheduleDto inputSchedule = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		ScheduleDto storedSchedule = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(2L)
				.build();
		
		when(scheduleDao.detailSchedule(1L)).thenReturn(storedSchedule);
		
		ScheduleResultDto result = scheduleService.updateSchedule(inputSchedule);
		
		assertEquals("unauthorized", result.getResult());
	}
	
	@Test
	void updateScheduleTest_Fail() {
		ScheduleDto inputSchedule = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		ScheduleDto storedSchedule = ScheduleDto.builder()
				.scheduleId(2L)
				.userId(1L)
				.build();
		
		when(scheduleDao.detailSchedule(1L)).thenReturn(storedSchedule);
		when(scheduleDao.updateSchedule(inputSchedule)).thenReturn(-1);
		
		ScheduleResultDto result = scheduleService.updateSchedule(inputSchedule);
		
		assertEquals("fail", result.getResult());
	}
	
	@Test
	void updateScheduleTest_DBFail() {
		ScheduleDto inputSchedule = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		ScheduleDto storedSchedule = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.build();
		
		when(scheduleDao.detailSchedule(1L)).thenThrow(new RuntimeException("DB Error"));
		
		ScheduleResultDto result = scheduleService.updateSchedule(inputSchedule);
		
		assertEquals("fail", result.getResult());
	}
}
