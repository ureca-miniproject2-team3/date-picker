package com.mycom.myapp.events.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventStatusDaoTest {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void completedCheckedEvents_정상_동작() {
        // given
        // 1. 확정(CHECKED) 상태의 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id, status) VALUES (1, '지난 확정 이벤트', 1, 'CHECKED')");
        // 2. 이전 날짜의 타임라인 생성 (확정 스케줄)
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 13, 14, 0); // 예시 시간
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 13, 16, 0);
        jdbcTemplate.update(
                "INSERT INTO timeline (event_id, start_time, end_time) VALUES (?, ?, ?)",
                1,
                Timestamp.valueOf(startTime),
                Timestamp.valueOf(endTime));

        // when
        eventDao.completedCheckedEvents();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM event WHERE id = 1", String.class);
        assertThat(status).isEqualTo("COMPLETED");
    }

    @Test
    void completedCheckedEvents_미래날짜_있으면_상태변경_안함() {
        // given
        // 1. 확정(CHECKED) 상태의 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id, status) VALUES (2, '미래 확정 이벤트', 1, 'CHECKED')");
        // 2. 이벤트에 미래 날짜 추가
        LocalDate futureDate = LocalDate.now().plusDays(1);
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (2, ?)", futureDate);

        // when
        eventDao.completedCheckedEvents();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM event WHERE id = 2", String.class);
        assertThat(status).isEqualTo("CHECKED");
    }

    @Test
    void expiredUncheckedEvents_정상_동작() {
        // given
        // 1. 미확정(UNCHECKED) 상태의 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id, status) VALUES (3, '지난 미확정 이벤트', 1, 'UNCHECKED')");
        // 2. 이벤트에 과거 날짜 추가
        LocalDate pastDate = LocalDate.now().minusDays(1);
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (3, ?)", pastDate);

        // when
        eventDao.expiredUncheckedEvents();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM event WHERE id = 3", String.class);
        assertThat(status).isEqualTo("EXPIRED");
    }

    @Test
    void expiredUncheckedEvents_미래날짜_있으면_상태변경_안함() {
        // given
        // 1. 미확정(UNCHECKED) 상태의 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id, status) VALUES (4, '미래 미확정 이벤트', 1, 'UNCHECKED')");
        // 2. 이벤트에 미래 날짜 추가
        LocalDate futureDate = LocalDate.now().plusDays(1);
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (4, ?)", futureDate);

        // when
        eventDao.expiredUncheckedEvents();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM event WHERE id = 4", String.class);
        assertThat(status).isEqualTo("UNCHECKED");
    }
}
