package com.mycom.myapp.events.service;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;

    @Override
    public EventResultDto listEvent(Long userId) {
        EventResultDto result = new EventResultDto();

        try {
            List<EventDto> eventDtoList = eventDao.listEvent(userId);

            result.setResult("success");
            result.setEventDtoList(eventDtoList);

        } catch (Exception e) {

            return handleException("이벤트 리스트 조회", e);
        }

        return result;
    }

    @Override
    @Transactional
    public EventResultDto createEvent(EventDto eventDto) {
        EventResultDto result = new EventResultDto();

        try {
            eventDao.insertEvent(eventDto);

            for (LocalDate eventDate : eventDto.getEventDates()) {
                eventDao.insertEventDate(eventDto.getEventId(), eventDate);
            }

            eventDao.insertUserEvent(eventDto.getUserId(), eventDto.getEventId());

            result.setResult("success");
            result.setEventDto(eventDto);

        } catch (Exception e) {

            return handleException("이벤트 생성", e);
        }

        return result;
    }

    @Override
    @Transactional
    public EventResultDto updateEvent(EventDto eventDto) {
        EventResultDto result = new EventResultDto();

        try {
            eventDao.updateEventTitle(eventDto);

            List<LocalDate> existingDates = eventDao.getExistingDates(eventDto.getEventId());

            // 새롭게 추가되는 날짜만 필터링
            List<LocalDate> newDates = eventDto.getEventDates();
            List<LocalDate> datesToInsert = newDates.stream()
                    .filter(date -> !existingDates.contains(date))
                    .toList();

            for (LocalDate date : datesToInsert) {
                eventDao.insertEventDate(eventDto.getEventId(), date);
            }

            result.setResult("success");

        } catch (Exception e) {

            return handleException("이벤트 수정", e);
        }

        return result;
    }

    @Override
    @Transactional
    public EventResultDto deleteEvent(Long eventId) {
        EventResultDto result = new EventResultDto();

        /*
        스케줄 삭제 구현 X - 2025.05.09
        이벤트 관련 테이블 데이터만 삭제 - event, event_date, user_event
         */
        try {
            eventDao.deleteUserEvent(eventId);
            eventDao.deleteEventDate(eventId);
            eventDao.deleteEvent(eventId);

            result.setResult("success");

        } catch (Exception e) {

            return handleException("이벤트 삭제", e);
        }

        return result;
    }

    private EventResultDto handleException(String operation, Exception e) {
        log.warn("{} 중 예외 발생: {}", operation, e.getMessage());

        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException ex) {
            log.warn("트랜잭션 활성화 안됨. 롤백 요청 무시 {}", ex.getMessage());
        }

        EventResultDto result = new EventResultDto();
        result.setResult("fail");
        return result;
    }

}
