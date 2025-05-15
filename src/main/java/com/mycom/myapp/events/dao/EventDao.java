package com.mycom.myapp.events.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventSummaryDto;
import com.mycom.myapp.events.dto.TimelineDto;
import com.mycom.myapp.notifications.dto.UserEventDto;

@Mapper
public interface EventDao {

    // 이벤트 리스트 조회
    List<EventSummaryDto> listEvent(@Param("userId") Long userId);
    // 이벤트 유저 조회
    List<String> findUserNamesByEventId(Long eventId);
    // 이벤트 상세 정보 조회
    EventDto detailEvent(@Param("eventId") Long eventId);
    List<Long> findUserIdsByEventId(@Param("eventId") Long eventId);
    List<LocalDate> findEventDatesByEventId(@Param("eventId") Long eventId);

    // 이벤트 등록
    void insertEvent(EventDto eventDto);
    void insertEventDate(@Param("eventId") Long eventId, @Param("eventDate") LocalDate eventDate);
    void insertUserEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);

    // 이벤트 수정
    void updateEventTitle(EventDto eventDto);

    // 이벤트 삭제
    void deleteEvent(@Param("eventId") Long eventId);
    void deleteEventDate(@Param("eventId") Long eventId);
    void deleteUserEvent(@Param("eventId") Long eventId);
    void deleteTimeline(@Param("eventId") Long eventId);

    // 이벤트 초대
    List<Long> getParticipantsByEventId(@Param("eventId") Long eventId);

    // 이벤트 상태 수정
    void completedCheckedEvents(); // 확정 -> 완료
    void expiredUncheckedEvents(); // 미확정 -> 만료

    // 이벤트 확정
    void checkEvent(@Param("eventId") Long eventId);
    void insertTimeline(TimelineDto timelineDto);

    // is_sent = false (알림 미수신)인 user_event 조회
    List<UserEventDto> listUnsentUserEvent(Long userId);
}
