package com.mycom.myapp.events.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.TimelineDto;
import com.mycom.myapp.events.dto.type.EventStatus;

@SpringBootTest
@Transactional
public class EventDetailDaoTest {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long eventId;
    private Long userId;
    private Long timelineId;
    private LocalDate eventDate;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        userId = 1L;
        eventDate = LocalDate.of(2025, 5, 1);

        // 테스트용 이벤트 생성
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id) VALUES (100, '테스트 이벤트1', ?)", userId);
        jdbcTemplate.update("INSERT INTO event (id, title, owner_id, status) VALUES (200, '테스트 이벤트2', ?, 'CHECKED')", userId);
        eventId = 100L;

        // 이벤트 날짜 추가
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (?, ?)", eventId, eventDate);
        jdbcTemplate.update("INSERT INTO event_date (event_id, event_date) VALUES (?, ?)", 200L, eventDate);
        
        // 사용자-이벤트 매핑 추가
        jdbcTemplate.update("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)", userId, eventId);
        jdbcTemplate.update("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)", userId, 200L);
        
        //
        timelineId = 100L;
        jdbcTemplate.update("INSERT INTO timeline (id, event_id, start_time, end_time) VALUES (?, ?, ?, ?)", timelineId, 200L,
        		java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 13, 00, 00)),
				java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 16, 00, 00)));
    }

    @Test
    void 이벤트_상세_조회_미확정_정상_동작() {
        // when
        EventDto dto = eventDao.detailEvent(eventId);
        dto.setUserIds(   eventDao.findUserIdsByEventId(eventId)   );
        dto.setEventDates(eventDao.findEventDatesByEventId(eventId));

        // then: 기본 필드 검증
        assertThat(dto.getEventId()).isEqualTo(eventId);
        assertThat(dto.getTitle()).isEqualTo("테스트 이벤트1");
        assertThat(dto.getOwnerId()).isEqualTo(userId);
        assertThat(dto.getCode()).isEqualTo("001");

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
    void 이벤트_상세_조회_확정_정상_동작() {
        // when
        EventDto dto = eventDao.detailEvent(200L);
        dto.setUserIds(   eventDao.findUserIdsByEventId(200L)   );
        dto.setEventDates(eventDao.findEventDatesByEventId(200L));
        // then: 기본 필드 검증
        assertThat(dto.getEventId()).isEqualTo(200L);
        assertThat(dto.getTitle()).isEqualTo("테스트 이벤트2");
        assertThat(dto.getOwnerId()).isEqualTo(userId);
        assertThat(dto.getCode()).isEqualTo("002");

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
        
        // 타임라인 검증
        TimelineDto timeline = dto.getTimeline();
        assertThat(timeline).isNotNull();
        assertThat(timeline.getEventId()).isEqualTo(200L);
        assertThat(timeline.getStartTime()).isEqualTo(LocalDateTime.of(2025, 5, 1, 13, 00, 00));
        assertThat(timeline.getEndTime()).isEqualTo(LocalDateTime.of(2025, 5, 1, 16, 00, 00));
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
