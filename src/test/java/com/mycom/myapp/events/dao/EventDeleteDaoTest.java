package com.mycom.myapp.events.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import com.mycom.myapp.events.dto.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventDeleteDaoTest {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 설정
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id) VALUES (?, ?, ?)", 1L, "테스트 이벤트", 1L);
        jdbcTemplate.update("INSERT INTO event_date (id, event_id, event_date) VALUES (?, ?, ?)", 1L, 1L, java.sql.Date.valueOf("2025-05-01"));
        jdbcTemplate.update("INSERT INTO event_date (id, event_id, event_date) VALUES (?, ?, ?)", 2L, 1L, java.sql.Date.valueOf("2025-05-02"));
        jdbcTemplate.update("INSERT INTO user_event (id, user_id, event_id) VALUES (?, ?, ?)", 1L, 1L, 1L);
        jdbcTemplate.update("INSERT INTO timeline (id, event_id, start_time, end_time) VALUES (?, ?, ?, ?)", 1L, 1L,
        		java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 13, 00, 00)),
				java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 16, 00, 00)));
        
    }

    @Test
    void deleteEvent_정상_동작() {
        // given
        Long eventId = 1L;

        // 외래키 제약조건 때문에 먼저 관련 데이터를 삭제해야 함
        eventDao.deleteUserEvent(eventId);
        eventDao.deleteEventDate(eventId);
        eventDao.deleteTimeline(eventId);

        // when
        eventDao.deleteEvent(eventId);

        // then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event WHERE id = ?",
                new Object[]{eventId},
                Integer.class
        );

        assertEquals(0, count);
    }

    @Test
    void deleteEventDate_정상_동작() {
        // given
        Long eventId = 1L;

        // when
        eventDao.deleteEventDate(eventId);

        // then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_date WHERE event_id = ?",
                new Object[]{eventId},
                Integer.class
        );

        assertEquals(0, count);
    }

    @Test
    void deleteUserEvent_정상_동작() {
        // given
        Long eventId = 1L;

        // when
        eventDao.deleteUserEvent(eventId);

        // then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_event WHERE event_id = ?",
                new Object[]{eventId},
                Integer.class
        );

        assertEquals(0, count);
    }
    
    @Test
    void deleteTimeline_정상_동작() {
    	// given
    	Long eventId = 1L;
    	
    	// when
    	eventDao.deleteTimeline(eventId);
    	
    	// then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM timeline WHERE event_id = ?",
                new Object[]{eventId},
                Integer.class
        );
        
        assertEquals(0, count);
    }
}
