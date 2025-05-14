package com.mycom.myapp.events.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.events.dto.TimelineDto;
import com.mycom.myapp.events.dto.type.EventStatus;

@SpringBootTest
@Transactional
public class EventCheckDaoTest {

	@Autowired
	private EventDao eventDao;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO event (id, title) VALUES (?, ?)", 1L, "테스트 이벤트");
        jdbcTemplate.update("INSERT INTO event_date (id, event_id, event_date) VALUES (?, ?, ?)", 1L, 1L, java.sql.Date.valueOf("2025-05-01"));
        jdbcTemplate.update("INSERT INTO event_date (id, event_id, event_date) VALUES (?, ?, ?)", 2L, 1L, java.sql.Date.valueOf("2025-05-02"));
    } 
    
    @Test
    void 이벤트_상태_확정_정상_동작() {
    	// given
    	Long eventId = 1L;
    	
    	// when
    	eventDao.checkEvent(eventId);
    	
    	// then
    	EventStatus updatedStatus = jdbcTemplate.queryForObject(
    			"SELECT status FROM event WHERE id = ?",
    			new Object[] {1L},
    			EventStatus.class
		);
    	
    	assertEquals(EventStatus.CHECKED, updatedStatus);
    }
    
    @Test
    void 이벤트_타임라인_확정_정상_동작() {
    	// given
    	TimelineDto timelineDto = TimelineDto.builder()
    			.eventId(1L)
    			.startTime(LocalDateTime.of(2025, 05, 01, 13, 00, 00))
    			.endTime(LocalDateTime.of(2025, 05, 01, 16, 00, 00))
    			.build();
    	
    	// when
    	eventDao.insertTimeline(timelineDto);
    	
    	// then
    	assertNotNull(timelineDto.getTimelineId());
    	
    }
}
