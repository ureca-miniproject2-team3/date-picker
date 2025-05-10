package com.mycom.myapp.events.dto;

import java.util.List;
import lombok.Data;

@Data
public class EventResultDto {

    private String result;
    private EventDto eventDto;
    private List<EventSummaryDto> eventDtoList;
}
