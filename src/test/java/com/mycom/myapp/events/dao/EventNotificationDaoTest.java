package com.mycom.myapp.events.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventNotificationDaoTest {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long eventId;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        // 테스트용 사용자 생성
        try {
            // 사용자 생성
            jdbcTemplate.update("INSERT INTO user (id, email, name, password) VALUES (?, 'test@test.com', 'Test User', 'password')", userId);
        } catch (Exception e) {
            // 이미 존재하는 경우 무시
        }

        // 테스트용 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id) VALUES (100, '테스트 이벤트', ?)", userId);
        eventId = 100L;

        // 이벤트 날짜 추가
        LocalDate eventDate = LocalDate.of(2025, 5, 1);
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (?, ?)", eventId, eventDate);

        // 사용자-이벤트 매핑 추가
        jdbcTemplate.update("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)", userId, eventId);

        // 알림 전송 상태 초기화 (필요한 경우)
        jdbcTemplate.update("UPDATE user_event SET is_sent = 0 WHERE user_id = ? AND event_id = ?", userId, eventId);
    }

    @Test
    void markAsSent_정상_동작() {
        // given
        // 초기 상태 확인
        Integer initialSentStatus = jdbcTemplate.queryForObject(
                "SELECT is_sent FROM user_event WHERE user_id = ? AND event_id = ?",
                Integer.class, userId, eventId);
        assertThat(initialSentStatus).isEqualTo(0);

        // when
        eventDao.markAsSent(userId, eventId);

        // then
        Integer updatedSentStatus = jdbcTemplate.queryForObject(
                "SELECT is_sent FROM user_event WHERE user_id = ? AND event_id = ?",
                Integer.class, userId, eventId);
        assertThat(updatedSentStatus).isEqualTo(1);
    }

    @Test
    void markAsSent_존재하지_않는_매핑() {
        // given
        Long nonExistentUserId = 999L;

        // when
        eventDao.markAsSent(nonExistentUserId, eventId);

        // then
        // 예외가 발생하지 않고 정상적으로 실행되는지 확인
        // 실제로는 아무 변화가 없어야 함
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_event WHERE user_id = ? AND event_id = ?",
                Integer.class, nonExistentUserId, eventId);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void markAsSent_이미_전송된_알림() {
        // given
        // 알림을 이미 전송된 상태로 설정
        jdbcTemplate.update("UPDATE user_event SET is_sent = 1 WHERE user_id = ? AND event_id = ?", userId, eventId);

        Integer initialSentStatus = jdbcTemplate.queryForObject(
                "SELECT is_sent FROM user_event WHERE user_id = ? AND event_id = ?",
                Integer.class, userId, eventId);
        assertThat(initialSentStatus).isEqualTo(1);

        // when
        eventDao.markAsSent(userId, eventId);

        // then
        // 상태가 여전히 1로 유지되어야 함
        Integer updatedSentStatus = jdbcTemplate.queryForObject(
                "SELECT is_sent FROM user_event WHERE user_id = ? AND event_id = ?",
                Integer.class, userId, eventId);
        assertThat(updatedSentStatus).isEqualTo(1);
    }
}
