package com.mycom.myapp.schedules.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.schedules.dto.ScheduleDto;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleDaoTest {

	@Autowired
	private ScheduleDao scheduleDao;
	
	@Autowired
	private static EventDao eventDao;
	
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
}
