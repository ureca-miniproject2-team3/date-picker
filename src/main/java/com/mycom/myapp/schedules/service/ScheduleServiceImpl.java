package com.mycom.myapp.schedules.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mycom.myapp.schedules.dao.ScheduleDao;
import com.mycom.myapp.schedules.dto.ScheduleDto;
import com.mycom.myapp.schedules.dto.ScheduleResultDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

	private final ScheduleDao scheduleDao;

	@Override
	public ScheduleResultDto insertSchedule(ScheduleDto scheduleDto) {
		ScheduleResultDto scheduleResultDto = new ScheduleResultDto();
		
		try {
			int ret = scheduleDao.insertSchedule(scheduleDto);
			
			if(ret == 1) scheduleResultDto.setResult("success");
			else scheduleResultDto.setResult("fail");
		} catch (Exception e) {
			e.printStackTrace();
			scheduleResultDto.setResult("fail");
		}
		
		return scheduleResultDto;
	}

	@Override
	public ScheduleResultDto listSchedule(Long eventId) {
		ScheduleResultDto scheduleResultDto = new ScheduleResultDto();
		
		try {
			List<ScheduleDto> scheduleDtoList = scheduleDao.listSchedule(eventId);
			
			scheduleResultDto.setScheduleDtoList(scheduleDtoList);
			scheduleResultDto.setResult("success");
		} catch (Exception e) {
			e.printStackTrace();
			scheduleResultDto.setResult("fail");
		}
		
		return scheduleResultDto;
	}

	@Override
	public ScheduleResultDto detailSchedule(Long scheduleId) {
		ScheduleResultDto scheduleResultDto = new ScheduleResultDto();
		
		try {
			ScheduleDto scheduleDto = scheduleDao.detailSchedule(scheduleId);
				
			scheduleResultDto.setScheduleDto(scheduleDto);
			scheduleResultDto.setResult("success");
		} catch (Exception e) {
			e.printStackTrace();
			scheduleResultDto.setResult("fail");
		}
		
		return scheduleResultDto;
	}
	
	
}
