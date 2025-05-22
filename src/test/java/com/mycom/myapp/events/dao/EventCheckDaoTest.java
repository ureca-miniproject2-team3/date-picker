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
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id) VALUES (?, ?, ?)", 100L, "테스트 이벤트", 1L);
        jdbcTemplate.update("INSERT INTO event_date (id, event_id, event_date) VALUES (?, ?, ?)", 2L, 100L, java.sql.Date.valueOf("2025-05-01"));
        jdbcTemplate.update("INSERT INTO event_date (id, event_id, event_date) VALUES (?, ?, ?)", 3L, 100L, java.sql.Date.valueOf("2025-05-02"));
    } 
    
    @Test
    void 이벤트_상태_확정_정상_동작() {
    	// given
    	Long eventId = 100L;
    	
    	// when
    	eventDao.checkEvent(eventId);
    	
    	// then
    	String updatedStatus = jdbcTemplate.queryForObject(
    			"SELECT code FROM event WHERE id = ?",
    			new Object[] {100L},
    			String.class
		);
    	
    	assertEquals("002", updatedStatus);
    }
    
    @Test
    void 이벤트_타임라인_확정_정상_동작() {
    	// given
    	TimelineDto timelineDto = TimelineDto.builder()
    			.eventId(100L)
    			.startTime(LocalDateTime.of(2025, 05, 01, 13, 00, 00))
    			.endTime(LocalDateTime.of(2025, 05, 01, 16, 00, 00))
    			.build();
    	
    	// when
    	eventDao.insertTimeline(timelineDto);
    	
    	// then
    	assertNotNull(timelineDto.getTimelineId());
    	
    }
}
