package com.mycom.myapp.schedules.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

	private Long scheduleId;
	
	private Long userId;
	
	private Long eventId;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
	
}
