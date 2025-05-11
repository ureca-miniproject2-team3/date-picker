package com.mycom.myapp.schedules.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
}
