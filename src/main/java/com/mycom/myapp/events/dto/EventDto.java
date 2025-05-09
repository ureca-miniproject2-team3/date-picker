package com.mycom.myapp.events.dto;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {

    private Long eventId;
    private String title;
    private List<Date> eventDates;
    private Long userId;
}
