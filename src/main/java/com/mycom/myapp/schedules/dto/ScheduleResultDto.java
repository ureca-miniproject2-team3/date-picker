package com.mycom.myapp.schedules.dto;

import java.util.List;

import lombok.Data;

@Data
public class ScheduleResultDto {

	private String result;
	
	private ScheduleDto scheduleDto;
	
	private List<ScheduleDto> scheduleDtoList;
	
}
