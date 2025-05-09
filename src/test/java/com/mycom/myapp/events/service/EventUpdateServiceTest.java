package com.mycom.myapp.events.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
    void 이벤트_수정중_예외발생() {
        // given
        EventDto dto = EventDto.builder()
                .eventId(1L)
                .title("수정 실패")
                .eventDates(List.of(LocalDate.of(2025, 5, 1)))
                .build();

        doThrow(new RuntimeException("DB 오류"))
                .when(eventDao).updateEventTitle(dto);

        // when / then
        assertThatThrownBy(() -> eventService.updateEvent(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB 오류");

        verify(eventDao).updateEventTitle(dto);

        // getExistingDates나 insert는 호출되지 않아야 함
        verify(eventDao, never()).getExistingDates(any());
        verify(eventDao, never()).insertEventDate(anyLong(), any());
    }
}
