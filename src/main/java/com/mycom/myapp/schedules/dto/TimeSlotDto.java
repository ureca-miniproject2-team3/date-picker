package com.mycom.myapp.schedules.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TimeSlotDto {

    private LocalDateTime start;
    private LocalDateTime end;
}
