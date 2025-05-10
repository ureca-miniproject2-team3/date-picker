package com.mycom.myapp.events.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventDetailServiceTest {
    private EventDao eventDao;
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventDao = mock(EventDao.class);
        eventService = new EventServiceImpl(eventDao);
    }

    @Test
    void 이벤트_상세_정상_조회() {
        // given
        Long eventId = 1L;
        Long ownerId = 42L;
        LocalDate d1 = LocalDate.of(2025, 5, 1);
        LocalDate d2 = LocalDate.of(2025, 5, 2);
        List<LocalDate> dates = Arrays.asList(d1, d2);

        EventDto mockEventDto = EventDto.builder()
                .eventId(eventId)
                .title("테스트 이벤트")
                .ownerId(ownerId)
                .eventDates(dates)
                .build();

        when(eventDao.detailEvent(eventId)).thenReturn(mockEventDto);

        // when
        EventResultDto result = eventService.detailEvent(eventId);

        // then
        assertEquals("success", result.getResult());
        assertEquals(mockEventDto, result.getEventDto());
        verify(eventDao).detailEvent(eventId);
    }

    @Test
    void 이벤트_상세_조회_이벤트_없음() {
        // given
        Long eventId = 999L;
        when(eventDao.detailEvent(eventId)).thenReturn(null);

        // when
        EventResultDto result = eventService.detailEvent(eventId);

        // then
        assertEquals("not found", result.getResult());
        assertNull(result.getEventDto());
        verify(eventDao).detailEvent(eventId);
    }

    @Test
    void 이벤트_상세_조회_예외_발생() {
        // given
        Long eventId = 1L;
        RuntimeException exception = new RuntimeException("데이터베이스 오류");
        when(eventDao.detailEvent(anyLong())).thenThrow(exception);

        // when
        EventResultDto result = eventService.detailEvent(eventId);

        // then
        assertEquals("fail", result.getResult());
        verify(eventDao).detailEvent(eventId);
    }
}