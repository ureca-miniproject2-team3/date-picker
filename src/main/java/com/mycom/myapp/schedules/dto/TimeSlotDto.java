package com.mycom.myapp.schedules.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeSlotDto {

    private LocalDateTime start;
    private LocalDateTime end;
    private List<Long> userIds;
}
