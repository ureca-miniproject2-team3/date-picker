package com.mycom.myapp.events.service;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;

    @Override
    @Transactional
    public EventResultDto createEvent(EventDto eventDto) {
        EventResultDto result = new EventResultDto();

        try {
            eventDao.insertEvent(eventDto);
            if (eventDto.getEventId() == null) {
                throw new IllegalStateException("이벤트 ID 생성 실패");
            }

            for (Date eventDate : eventDto.getEventDates()) {
                if (eventDate == null) throw new IllegalArgumentException("이벤트 날짜는 null일 수 없습니다.");
                eventDao.insertEventDate(eventDto.getEventId(), eventDate);
            }

            if (eventDto.getUserId() == null) {
                throw new IllegalArgumentException("유저 ID가 누락되었습니다.");
            }
            eventDao.insertUserEvent(eventDto.getUserId(), eventDto.getEventId());

            result.setResult("success");
            result.setEventDto(eventDto);

        } catch (IllegalArgumentException e) {
            log.warn("입력 값 오류: {}", e.getMessage());
            result.setResult("invalid input");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        } catch (DataAccessException e) {
            log.error("DB 접근 오류: {}", e.getMessage(), e);
            result.setResult("db_error");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
            result.setResult("unknown_error");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return result;
    }
}
