package com.mycom.myapp.schedules.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.schedules.dto.ScheduleDto;

@Mapper
public interface ScheduleDao {

	int insertSchedule(ScheduleDto scheduleDto);
	
	List<ScheduleDto> listSchedule(Long eventId);
	
	ScheduleDto detailSchedule(Long scheduleId);
	
}
