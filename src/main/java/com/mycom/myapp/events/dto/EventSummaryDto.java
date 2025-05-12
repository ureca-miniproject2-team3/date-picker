package com.mycom.myapp.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class EventSummaryDto {
    private Long eventId;
    private String title;
    private List<Long> userIds;
    private List<String> userNames;
}
