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

            for (LocalDate eventDate : eventDto.getEventDates()) {
                eventDao.insertEventDate(eventDto.getEventId(), eventDate);
            }

            eventDao.insertUserEvent(eventDto.getUserId(), eventDto.getEventId());

            result.setResult("success");
            result.setEventDto(eventDto);

        } catch (Exception e) {
            log.warn("이벤트 생성 중 예외 발생: {}", e.getMessage());
            result.setResult("fail");
            throw e;
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
            log.warn("이벤트 수정 중 예외 발생: {}", e.getMessage());
            result.setResult("fail");
            throw e;
        }

        return result;
    }
}
