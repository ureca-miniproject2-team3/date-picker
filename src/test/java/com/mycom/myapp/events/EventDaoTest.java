package com.mycom.myapp.events;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventDaoTest {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 이벤트_등록_정상_동작() {
        // given
        EventDto eventDto = EventDto.builder()
                .title("회의")
                .userId(1L)
                .eventDates(List.of(LocalDate.of(2025, 5, 1)))
                .build();

        // when
        eventDao.insertEvent(eventDto);

        // then
        assertThat(eventDto.getEventId()).isNotNull();
    }

    @Test
    void 이벤트_날짜_등록_정상_동작() {
        // given
        jdbcTemplate.update("INSERT INTO event (id, title) VALUES (1, '테스트 이벤트')");

        Long eventId = 1L;
        LocalDate date = LocalDate.of(2025, 5, 1);

        // when
        eventDao.insertEventDate(eventId, date);

        // then
        // 에러 없이 끝나면 통과
    }

    @Test
    void 유저_이벤트_매핑_정상_동작() {
        // given
        jdbcTemplate.update("INSERT INTO user (id, name, email, password) VALUES (1, '테스트', 'test@example.com', '1234')");
        jdbcTemplate.update("INSERT INTO event (id, title) VALUES (1, '테스트 이벤트')");

        Long userId = 1L;
        Long eventId = 1L;

        // when
        eventDao.insertUserEvent(userId, eventId);

        // then
        // 에러 없이 끝나면 통과
    }
}
