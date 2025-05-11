package com.mycom.myapp.schedules.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleDto {

	private Long scheduleId;
	
	private Long userId;
	
	private Long eventId;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
	
}
