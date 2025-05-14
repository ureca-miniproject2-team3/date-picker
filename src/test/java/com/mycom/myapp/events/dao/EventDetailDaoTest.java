package com.mycom.myapp.events.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.type.EventStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventDetailDaoTest {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long eventId;
    private Long userId;
    private LocalDate eventDate;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        userId = 1L;
        eventDate = LocalDate.of(2025, 5, 1);

        // 테스트용 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id) VALUES (100, '테스트 이벤트', ?)", userId);
        eventId = 100L;

        // 이벤트 날짜 추가
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (?, ?)", eventId, eventDate);

        // 사용자-이벤트 매핑 추가
        jdbcTemplate.update("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)", userId, eventId);
    }

    @Test
    void 이벤트_상세_조회_정상_동작() {
        // when
        EventDto dto = eventDao.detailEvent(eventId);
        dto.setUserIds(   eventDao.findUserIdsByEventId(eventId)   );
        dto.setEventDates(eventDao.findEventDatesByEventId(eventId));

        // then: 기본 필드 검증
        assertThat(dto.getEventId()).isEqualTo(eventId);
        assertThat(dto.getTitle()).isEqualTo("테스트 이벤트");
        assertThat(dto.getOwnerId()).isEqualTo(userId);
        assertThat(dto.getStatus()).isEqualTo(EventStatus.UNCHECKED);

        // 참여자 리스트 검증
        List<Long> userIds = dto.getUserIds();
        assertThat(userIds).isNotNull();
        assertThat(userIds.size()).isEqualTo(1);
        assertThat(userIds.get(0)).isEqualTo(userId);

        // 날짜 리스트 검증
        List<LocalDate> dates = dto.getEventDates();
        assertThat(dates).isNotNull();
        assertThat(dates.size()).isEqualTo(1);
        assertThat(dates.get(0)).isEqualTo(eventDate);
    }

    @Test
    void 이벤트_참여자_조회_정상_동작() {
        // when
        List<Long> userIds = eventDao.findUserIdsByEventId(eventId);

        // then
        assertThat(userIds).isNotNull();
        assertThat(userIds.size()).isEqualTo(1);
        assertThat(userIds.get(0)).isEqualTo(userId);
    }

    @Test
    void 이벤트_날짜_조회_정상_동작() {
        // when
        List<LocalDate> dates = eventDao.findEventDatesByEventId(eventId);

        // then
        assertThat(dates).isNotNull();
        assertThat(dates.size()).isEqualTo(1);
        assertThat(dates.get(0)).isEqualTo(eventDate);
    }
}
