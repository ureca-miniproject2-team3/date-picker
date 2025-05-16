package com.mycom.myapp.events.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineDto {

	private Long timelineId;
	private Long eventId;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	
}
