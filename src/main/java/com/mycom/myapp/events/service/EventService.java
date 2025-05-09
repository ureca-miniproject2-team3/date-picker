package com.mycom.myapp.events.service;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;

public interface EventService {

    EventResultDto createEvent(EventDto eventDto);
}
