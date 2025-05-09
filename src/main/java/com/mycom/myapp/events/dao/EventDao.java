package com.mycom.myapp.events.dao;

import com.mycom.myapp.events.dto.EventDto;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EventDao {

    // 이벤트 리스트 조회
    List<EventDto> listEvent(@Param("userId") Long userId);

    // 이벤트 등록
    void insertEvent(EventDto eventDto);
    void insertEventDate(@Param("eventId") Long eventId, @Param("eventDate") LocalDate eventDate);
    void insertUserEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);

    // 이벤트 수정
    void updateEventTitle(EventDto eventDto);
    List<LocalDate> getExistingDates(@Param("eventId") Long eventId);

    // 이벤트 삭제
    void deleteEvent(@Param("eventId") Long eventId);
    void deleteEventDate(@Param("eventId") Long eventId);
    void deleteUserEvent(@Param("eventId") Long eventId);
}
