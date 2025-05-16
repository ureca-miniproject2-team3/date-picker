package com.mycom.myapp.events.service;

import java.util.List;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.dto.TimelineDto;

public interface EventService {

    EventResultDto listEvent(Long userId);

    EventResultDto detailEvent(Long eventId);

    EventResultDto createEvent(EventDto eventDto);

    EventResultDto updateEvent(EventDto eventDto);

    EventResultDto deleteEvent(Long eventId, Long userId);

    EventResultDto inviteUserToEvent(Long inviterId, Long eventId, List<Long> invitedIds);

    EventResultDto updateEventStatus();

    EventResultDto checkEvent(Long userId, TimelineDto timelineDto);
}
