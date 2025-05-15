package com.mycom.myapp.events.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.notifications.dto.UserEventDto;

@SpringBootTest
@Transactional
public class UnsentUserEventListTest {
	
    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // 기존 테스트 데이터 정리
        jdbcTemplate.update("DELETE FROM schedule");
        jdbcTemplate.update("DELETE FROM user_event");
        jdbcTemplate.update("DELETE FROM event_date");
        jdbcTemplate.update("DELETE FROM timeline");
        jdbcTemplate.update("DELETE FROM event");

        // 이벤트 데이터 추가
        jdbcTemplate.update("INSERT INTO event (id, title, status) VALUES (?, ?, ?)", 100L, "테스트 이벤트1", "CHECKED");
        jdbcTemplate.update("INSERT INTO event (id, title, status) VALUES (?, ?, ?)", 200L, "테스트 이벤트2", "UNCHECKED");

        // 사용자-이벤트 매핑 데이터 추가 (기존 사용자 ID 사용)
        jdbcTemplate.update("INSERT INTO user_event (id, user_id, event_id) VALUES (?, ?, ?)", 100L, 1L, 100L);
        jdbcTemplate.update("INSERT INTO user_event (id, user_id, event_id, is_sent) VALUES (?, ?, ?, ?)", 200L, 1L, 200L, 1);
    }
    
    @Test
    void listUnsentUserEvent_정상_동작() {
    	// given
    	Long userId = 1L;
    	
    	// when
    	List<UserEventDto> userEvents = eventDao.listUnsentUserEvent(userId);
    	
    	// then
    	assertEquals(1, userEvents.size());
    	assertEquals(1L, userEvents.get(0).getUserId());
    	assertEquals(100L, userEvents.get(0).getEventId());
    	assertEquals(0, userEvents.get(0).getIsSent());
    	
    }
    
    @Test
    void updateUnsentUserEvent_정상_동작() {
    	// given
    	Long userId = 1L;
    	Long eventId = 100L;
    	
    	// when
    	eventDao.updateUnsentUserEvent(userId, eventId);
    	
    	// then
        Integer notSentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_event WHERE user_id = ? AND is_sent = 0",
                Integer.class,
                userId);
        
        Integer sentCount = jdbcTemplate.queryForObject(
        		"SELECT COUNT(*) FROM user_event WHERE user_id = ? AND is_sent = 1",
        		Integer.class,
        		userId);

        assertEquals(0, notSentCount);
        assertEquals(2, sentCount);
    }
}
