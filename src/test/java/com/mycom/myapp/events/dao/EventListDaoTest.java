package com.mycom.myapp.events.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.events.dto.EventSummaryDto;
import com.mycom.myapp.events.dto.type.EventStatus;

@SpringBootTest
@Transactional
public class EventListDaoTest {

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
        jdbcTemplate.update("INSERT INTO user_event (id, user_id, event_id) VALUES (?, ?, ?)", 200L, 1L, 200L);
        
        // 타임라인 추가
        jdbcTemplate.update("INSERT INTO timeline (id, event_id, start_time, end_time) VALUES (?, ?, ?, ?)", 100L, 100L,
        		java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 13, 00, 00)),
				java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 16, 00, 00)));
    }

    @Test
    void listEvent_정상_동작() {
        // given
        Long userId = 1L;

        // when
        List<EventSummaryDto> events = eventDao.listEvent(userId);

        // then
        assertNotNull(events);
        assertEquals(2, events.size());

        // 이벤트 ID와 제목 확인
        boolean foundEvent1 = false;
        boolean foundEvent2 = false;

        for (EventSummaryDto event : events) {
            if (event.getEventId() == 100L && "테스트 이벤트1".equals(event.getTitle())
                    && event.getStatus() == EventStatus.CHECKED
                    && event.getTimeline().getStartTime().equals(LocalDateTime.of(2025, 5, 1, 13, 0, 0))
                    && event.getTimeline().getEndTime().equals(LocalDateTime.of(2025, 5, 1, 16, 0, 0))) {
                foundEvent1 = true;
            } else if (event.getEventId() == 200L && "테스트 이벤트2".equals(event.getTitle())
                    && event.getStatus() == EventStatus.UNCHECKED
                    && event.getTimeline() == null) {
                foundEvent2 = true;
            }
        }

        assertTrue(foundEvent1, "이벤트1을 찾을 수 없습니다");
        assertTrue(foundEvent2, "이벤트2를 찾을 수 없습니다");
    }

    @Test
    void listEvent_사용자가_참여한_이벤트가_없는_경우() {
        // given
        Long userId = 3L; // 이벤트에 참여하지 않은 사용자

        // when
        List<EventSummaryDto> events = eventDao.listEvent(userId);

        // then
        assertNotNull(events);
        assertEquals(0, events.size());
    }
}
