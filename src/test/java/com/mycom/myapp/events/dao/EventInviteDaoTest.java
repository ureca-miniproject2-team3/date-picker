package com.mycom.myapp.events.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventInviteDaoTest {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long eventId;
    private Long ownerId;
    private Long participantId1;
    private Long participantId2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        ownerId = 1L;
        participantId1 = 2L;
        participantId2 = 3L;

        // 테스트용 사용자 생성 (필요한 경우)
        try {
            // 사용자 1 (소유자) 생성 - 이미 존재할 수 있음
            jdbcTemplate.update("INSERT INTO user (id, email, name, password) VALUES (?, 'owner@test.com', 'Owner', 'password')", ownerId);
        } catch (Exception e) {
            // 이미 존재하는 경우 무시
        }

        try {
            // 사용자 2 (참가자1) 생성
            jdbcTemplate.update("INSERT INTO user (id, email, name, password) VALUES (?, 'participant1@test.com', 'Participant1', 'password')", participantId1);
        } catch (Exception e) {
            // 이미 존재하는 경우 무시
        }

        try {
            // 사용자 3 (참가자2) 생성
            jdbcTemplate.update("INSERT INTO user (id, email, name, password) VALUES (?, 'participant2@test.com', 'Participant2', 'password')", participantId2);
        } catch (Exception e) {
            // 이미 존재하는 경우 무시
        }

        // 테스트용 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id) VALUES (100, '테스트 이벤트', ?)", ownerId);
        eventId = 100L;

        // 이벤트 날짜 추가
        LocalDate eventDate = LocalDate.of(2025, 5, 1);
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (?, ?)", eventId, eventDate);

        // 사용자-이벤트 매핑 추가 (소유자)
        jdbcTemplate.update("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)", ownerId, eventId);

        // 사용자-이벤트 매핑 추가 (참가자1)
        jdbcTemplate.update("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)", participantId1, eventId);

        // 사용자-이벤트 매핑 추가 (참가자2)
        jdbcTemplate.update("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)", participantId2, eventId);
    }

    @Test
    void 이벤트_참가자_조회_정상_동작() {
        // when
        List<Long> participants = eventDao.getParticipantsByEventId(eventId);

        // then
        assertThat(participants).isNotNull();
        assertThat(participants.size()).isEqualTo(3);
        assertThat(participants.contains(ownerId)).isTrue();
        assertThat(participants.contains(participantId1)).isTrue();
        assertThat(participants.contains(participantId2)).isTrue();
    }

    @Test
    void 존재하지_않는_이벤트_참가자_조회() {
        // when
        List<Long> participants = eventDao.getParticipantsByEventId(999L);

        // then
        assertThat(participants).isNotNull();
        assertThat(participants.size()).isEqualTo(0);
    }
}
