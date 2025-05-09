package com.mycom.myapp.events.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {

    private Long eventId;
    private String title;
    private List<LocalDate> eventDates;
    private Long userId;
}
