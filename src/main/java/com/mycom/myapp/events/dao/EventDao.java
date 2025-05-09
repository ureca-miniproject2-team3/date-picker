package com.mycom.myapp.events.dao;

import com.mycom.myapp.events.dto.EventDto;
import java.util.Date;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EventDao {

    void insertEvent(EventDto eventDto);

    void insertEventDate(@Param("eventId") Long eventId, @Param("eventDate") Date eventDate);

    void insertUserEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);
}
