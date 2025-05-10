package com.mycom.myapp.events.service;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import java.util.List;

public interface EventService {

    EventResultDto listEvent(Long userId);

    EventResultDto detailEvent(Long eventId);

    EventResultDto createEvent(EventDto eventDto);

    EventResultDto updateEvent(EventDto eventDto);

    EventResultDto deleteEvent(Long eventId);

    EventResultDto inviteUserToEvent(Long inviterId, Long eventId, List<Long> invitedIds);
}
