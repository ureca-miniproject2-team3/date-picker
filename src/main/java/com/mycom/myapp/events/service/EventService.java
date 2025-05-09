package com.mycom.myapp.events.service;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;

public interface EventService {

    EventResultDto listEvent(Long userId);

    EventResultDto createEvent(EventDto eventDto);

    EventResultDto updateEvent(EventDto eventDto);

    EventResultDto deleteEvent(Long eventId);
}
