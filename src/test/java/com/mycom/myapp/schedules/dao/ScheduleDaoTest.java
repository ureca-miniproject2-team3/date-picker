package com.mycom.myapp.schedules.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.schedules.dto.ScheduleDto;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleDaoTest {

	@Autowired
	private ScheduleDao scheduleDao;
	
	@Test
	void insertScheduleTest() {
		ScheduleDto scheduleDto = ScheduleDto.builder()	
							.userId(1L)
							.eventId(205L)
							.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
							.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
							.build();
		
		int ret = scheduleDao.insertSchedule(scheduleDto);
		
		assertEquals(1, ret);
	}
	
	@Test
	void listScheduleTest() {
		ScheduleDto scheduleDto = ScheduleDto.builder()	
				.userId(1L)
				.eventId(205L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();

		scheduleDao.insertSchedule(scheduleDto);
		
		List<ScheduleDto> scheduleDtoList = scheduleDao.listSchedule(205L);
		
		assertNotNull(scheduleDtoList);
	}
	
	@Test
	void detailScheduleTest() {
		ScheduleDto scheduleDto = ScheduleDto.builder()	
				.userId(1L)
				.eventId(205L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();

		scheduleDao.insertSchedule(scheduleDto);
		
		System.out.println(scheduleDto.getScheduleId());
		
		ScheduleDto findScheduleDto = scheduleDao.detailSchedule(scheduleDto.getScheduleId());
		
		assertEquals(scheduleDto.getScheduleId(),findScheduleDto.getScheduleId());
		assertEquals(1L, findScheduleDto.getUserId());
		assertEquals(205L, findScheduleDto.getEventId());
		assertEquals(LocalDateTime.of(2025, 5, 15, 13, 00, 00), findScheduleDto.getStartTime());
		assertEquals(LocalDateTime.of(2025, 5, 15, 15, 00, 00), findScheduleDto.getEndTime());
	}
	
	@Test
	void updateScheduleTest() {
		ScheduleDto scheduleDto = ScheduleDto.builder()	
				.userId(1L)
				.eventId(205L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();

		scheduleDao.insertSchedule(scheduleDto);
		
		ScheduleDto updateScheduleDto = ScheduleDto.builder()
				.scheduleId(scheduleDto.getScheduleId())
				.startTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 18, 00, 00))
				.build();
		
		int ret = scheduleDao.updateSchedule(updateScheduleDto);
		
		assertEquals(1, ret);
	}
}
