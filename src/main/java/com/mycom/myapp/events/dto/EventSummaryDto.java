package com.mycom.myapp.events.dto;

import lombok.Data;
import java.util.List;

import com.mycom.myapp.events.dto.type.EventStatus;

@Data
public class EventSummaryDto {
    private Long eventId;
    private String title;
    private List<Long> userIds;
    private List<String> userNames;
    private String code;
//    private EventStatus status;
    private TimelineDto timeline;
}
