package com.mycom.myapp.schedules.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.schedules.dto.ScheduleDto;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScheduleDao {

	int insertSchedule(ScheduleDto scheduleDto);

	int deleteSchedule(@Param("scheduleId") Long scheduleId);
	
	List<ScheduleDto> listSchedule(Long eventId);
	
	ScheduleDto detailSchedule(Long scheduleId);
	
	int updateSchedule(ScheduleDto scheduleDto);
	
}
