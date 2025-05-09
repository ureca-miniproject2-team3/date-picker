package com.mycom.myapp.events.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.mycom.myapp.events.dto.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventUpdateDaoTest {

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
    void 이벤트_제목_수정_정상_동작() {
        // given
        EventDto eventDto = EventDto.builder()
                .eventId(1L)
                .title("회의")
                .build();

        // when
        eventDao.updateEventTitle(eventDto);

        // then
        String updatedTitle = jdbcTemplate.queryForObject(
                "SELECT title FROM event WHERE id = ?",
                new Object[]{1L},
                String.class
        );

        assertEquals("회의", updatedTitle);
    }
}
