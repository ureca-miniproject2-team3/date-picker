package com.mycom.myapp.schedules.service;

import com.mycom.myapp.schedules.dto.TimeSlotDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.TreeMap;
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

	@Override
	public ScheduleResultDto getMaxOverlapSlots(Long eventId) {
		ScheduleResultDto scheduleResultDto = new ScheduleResultDto();

		try {
			List<ScheduleDto> schedules = scheduleDao.listSchedule(eventId);

			TreeMap<LocalDateTime, List<Long>> startMap = new TreeMap<>();
			TreeMap<LocalDateTime, List<Long>> endMap = new TreeMap<>();

			for (ScheduleDto s : schedules) {
				startMap
						.computeIfAbsent(s.getStartTime(), t -> new ArrayList<>())
						.add(s.getUserId());
				endMap
						.computeIfAbsent(s.getEndTime(), t -> new ArrayList<>())
						.add(s.getUserId());
			}

			List<LocalDateTime> times = new ArrayList<>();
			times.addAll(startMap.keySet());
			times.addAll(endMap.keySet());
			Collections.sort(times);

			Set<Long> activeUsers = new HashSet<>();
			int maxCount = 0;

			for (LocalDateTime t : times) {
				List<Long> ends = endMap.get(t);
				if (ends != null) ends.forEach(activeUsers::remove);

				List<Long> starts = startMap.get(t);
				if (starts != null) activeUsers.addAll(starts);

				maxCount = Math.max(maxCount, activeUsers.size());
			}

			activeUsers.clear();
			List<TimeSlotDto> slots = new ArrayList<>();

			for (int i = 0; i < times.size() - 1; i++) {
				LocalDateTime t = times.get(i);
				LocalDateTime nextT = times.get(i + 1);

				List<Long> ends = endMap.get(t);
				if (ends != null) ends.forEach(activeUsers::remove);

				List<Long> starts = startMap.get(t);
				if (starts != null) activeUsers.addAll(starts);

				if (activeUsers.size() == maxCount) {
					List<Long> usersSnapshot = new ArrayList<>(activeUsers);
					slots.add(new TimeSlotDto(t, nextT, usersSnapshot));
				}
			}

			scheduleResultDto.setResult("success");
			scheduleResultDto.setMaxCount(maxCount);
			scheduleResultDto.setTimeSlots(slots);

		} catch (Exception e) {
			e.printStackTrace();
			scheduleResultDto.setResult("fail");
		}

		return scheduleResultDto;
	}


}
