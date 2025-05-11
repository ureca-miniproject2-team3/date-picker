package com.mycom.myapp.schedules.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.schedules.dto.ScheduleDto;

@Mapper
public interface ScheduleDao {

	int insertSchedule(ScheduleDto scheduleDto);
	
}
