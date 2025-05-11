package com.mycom.myapp.schedules.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.myapp.schedules.dto.ScheduleDto;
import com.mycom.myapp.schedules.dto.ScheduleResultDto;
import com.mycom.myapp.schedules.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {
	
	private final ScheduleService scheduleService;

	@PostMapping("/schedules")
	public ResponseEntity<ScheduleResultDto> insertSchedule(ScheduleDto scheduleDto) {
		ScheduleResultDto scheduleResultDto = scheduleService.insertSchedule(scheduleDto);
		
		if("success".equals(scheduleResultDto.getResult())) {
			return ResponseEntity.ok(scheduleResultDto);
		} 
		else {
			return ResponseEntity.internalServerError().body(scheduleResultDto);
		}
	}
}
