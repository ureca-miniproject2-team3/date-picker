package com.mycom.myapp.events.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EventUpdateServiceTest {

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 이벤트_정상_수정() {
        // given
        EventDto dto = EventDto.builder()
                .eventId(1L)
                .title("수정된 제목")
                .eventDates(List.of(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 3)))
                .build();

        when(eventDao.getExistingDates(1L))
                .thenReturn(List.of(LocalDate.of(2025, 5, 1)));

        // when
        EventResultDto result = eventService.updateEvent(dto);

        // then
        verify(eventDao).updateEventTitle(dto);
        verify(eventDao).getExistingDates(1L);
        verify(eventDao).insertEventDate(1L, LocalDate.of(2025, 5, 3));

        assertEquals("success", result.getResult());
    }

    @Test
    void 중복되지_않은_날짜만_추가된다() {
        // given
        Long eventId = 1L;

        EventDto dto = EventDto.builder()
                .eventId(eventId)
                .title("중복 제거 테스트")
                .eventDates(List.of(
                        LocalDate.of(2025, 5, 1),  // 기존에 존재하는 날짜
                        LocalDate.of(2025, 5, 2),  // 새로 추가할 날짜
                        LocalDate.of(2025, 5, 3)   // 새로 추가할 날짜
                ))
                .build();

        when(eventDao.getExistingDates(eventId))
                .thenReturn(List.of(
                        LocalDate.of(2025, 5, 1)  // 이미 등록된 날짜
                ));

        // when
        EventResultDto result = eventService.updateEvent(dto);

        // then
        verify(eventDao).updateEventTitle(dto);
        verify(eventDao).getExistingDates(eventId);
        verify(eventDao, times(1)).insertEventDate(eventId, LocalDate.of(2025, 5, 2));
        verify(eventDao, times(1)).insertEventDate(eventId, LocalDate.of(2025, 5, 3));
        verify(eventDao, never()).insertEventDate(eventId, LocalDate.of(2025, 5, 1)); // 기존 날짜는 insert 안 됨

        assertEquals("success", result.getResult());
    }

    @Test
    void 이벤트_수정중_예외발생() {
        // given
        EventDto dto = EventDto.builder()
                .eventId(1L)
                .title("수정 실패")
                .eventDates(List.of(LocalDate.of(2025, 5, 1)))
                .build();

        doThrow(new RuntimeException("DB 오류"))
                .when(eventDao).updateEventTitle(dto);

        // when
        EventResultDto result = eventService.updateEvent(dto);

        // then
        assertEquals("fail", result.getResult());
        verify(eventDao).updateEventTitle(dto);

        // getExistingDates나 insert는 호출되지 않아야 함
        verify(eventDao, never()).getExistingDates(any());
        verify(eventDao, never()).insertEventDate(anyLong(), any());

    }
}
