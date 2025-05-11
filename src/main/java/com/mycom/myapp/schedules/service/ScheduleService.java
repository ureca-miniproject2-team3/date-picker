package com.mycom.myapp.schedules.service;

import com.mycom.myapp.schedules.dto.ScheduleDto;
import com.mycom.myapp.schedules.dto.ScheduleResultDto;

public interface ScheduleService {
	
	ScheduleResultDto insertSchedule(ScheduleDto scheduleDto);
	
	ScheduleResultDto listSchedule(Long eventId);
	
	ScheduleResultDto detailSchedule(Long scheduleId);

	ScheduleResultDto getMaxOverlapSlots(Long eventId);
}
