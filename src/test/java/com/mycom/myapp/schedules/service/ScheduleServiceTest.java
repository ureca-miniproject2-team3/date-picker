package com.mycom.myapp.schedules.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

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
}
