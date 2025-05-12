package com.mycom.myapp.events.service;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.dto.EventSummaryDto;
import com.mycom.myapp.schedules.dao.ScheduleDao;
import com.mycom.myapp.schedules.dto.ScheduleDto;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    private final ScheduleDao scheduleDao;

    @Override
    public EventResultDto listEvent(Long userId) {
        EventResultDto result = new EventResultDto();

        try {
            List<EventSummaryDto> eventDtoList = eventDao.listEvent(userId);
            for (EventSummaryDto summary : eventDtoList) {
                Long eventId = summary.getEventId();

                List<Long> userIds = eventDao.findUserIdsByEventId(eventId);
                summary.setUserIds(userIds);

                List<String> userNames = eventDao.findUserNamesByEventId(eventId);
                summary.setUserNames(userNames);
            }

            result.setResult("success");
            result.setEventDtoList(eventDtoList);

        } catch (Exception e) {
            return handleException("이벤트 리스트 조회", e);
        }

        return result;
    }

    @Override
    public EventResultDto detailEvent(Long eventId) {
        EventResultDto result = new EventResultDto();

        try {
            EventDto eventDto = eventDao.detailEvent(eventId);
            eventDto.setUserIds(eventDao.findUserIdsByEventId(eventId));
            eventDto.setEventDates(eventDao.findEventDatesByEventId(eventId));

            if (eventDto.getEventId() == null) {
                result.setResult("not found");

            } else {
                result.setResult("success");
                result.setEventDto(eventDto);
            }

        } catch (Exception e) {

            return handleException("이벤트 상세 조회", e);
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

            eventDao.insertUserEvent(eventDto.getOwnerId(), eventDto.getEventId());

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

            List<LocalDate> existingDates = eventDao.findEventDatesByEventId(eventDto.getEventId());

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
    public EventResultDto deleteEvent(Long eventId, Long userId) {
        EventResultDto result = new EventResultDto();

        try {
            Long ownerId = eventDao.detailEvent(eventId).getOwnerId();

            if (!Objects.equals(userId, ownerId)) {
                result.setResult("forbidden");

            } else {
                List<Long> scheduleIds = scheduleDao.listSchedule(eventId).stream()
                        .map(ScheduleDto::getScheduleId)
                        .toList();

                for (Long scheduleId : scheduleIds) {
                    scheduleDao.deleteSchedule(scheduleId);
                }

                eventDao.deleteUserEvent(eventId);
                eventDao.deleteEventDate(eventId);
                eventDao.deleteEvent(eventId);

                result.setResult("success");
            }

        } catch (Exception e) {

            return handleException("이벤트 삭제", e);
        }

        return result;
    }

    @Override
    public EventResultDto inviteUserToEvent(Long inviterId, Long eventId, List<Long> invitedIds) {
        EventResultDto result = new EventResultDto();

        try {
            Long ownerId = eventDao.detailEvent(eventId).getOwnerId();

            if (!Objects.equals(ownerId, inviterId)) {
                result.setResult("forbidden");

            } else {
                List<Long> participantsIds = eventDao.getParticipantsByEventId(eventId);

                List<Long> newInvitedUserIds = invitedIds.stream()
                        .filter(invitedId -> !participantsIds.contains(invitedId))
                        .toList();

                for (Long invitedId : newInvitedUserIds) {
                    eventDao.insertUserEvent(invitedId, eventId);
                }

                result.setResult("success");
            }

        } catch (Exception e) {

            return handleException("이벤트 초대", e);
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
