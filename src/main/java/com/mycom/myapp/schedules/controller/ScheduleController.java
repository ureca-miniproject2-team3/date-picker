package com.mycom.myapp.schedules.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.myapp.schedules.dto.ScheduleDto;
import com.mycom.myapp.schedules.dto.ScheduleResultDto;
import com.mycom.myapp.schedules.service.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name="Schedule", description="스케줄 관련 API")
public class ScheduleController {
	
	private final ScheduleService scheduleService;

	@PostMapping("/schedules")
	@Operation(summary="스케줄 등록", description="이벤트 ID, 유저 ID와 일치하는 시작 시간, 종료 시간을 저장하는 스케줄을 등록합니다.")
	public ResponseEntity<ScheduleResultDto> insertSchedule(ScheduleDto scheduleDto) {
		ScheduleResultDto scheduleResultDto = scheduleService.insertSchedule(scheduleDto);
		
		if("success".equals(scheduleResultDto.getResult())) {
			return ResponseEntity.ok(scheduleResultDto);
		} 
		else {
			return ResponseEntity.internalServerError().body(scheduleResultDto);
		}
	}
	
	@GetMapping("/event/{eventId}/schedules")
	@Operation(summary="스케줄 목록 조회", description="이벤트 ID와 일치하는 모든 스케줄을 조회합니다.")
	public ResponseEntity<ScheduleResultDto> listSchedule(@PathVariable("eventId") Long eventId) {
		ScheduleResultDto scheduleResultDto = scheduleService.listSchedule(eventId);
		
		if("success".equals(scheduleResultDto.getResult())) {
			return ResponseEntity.ok(scheduleResultDto);
		} 
		else {
			return ResponseEntity.internalServerError().body(scheduleResultDto);
		}
	}
	
	@GetMapping("/schedules/{scheduleId}")
	@Operation(summary="스케줄 상세 조회", description="스케줄 ID와 일치하는 스케줄을 조회합니다.")
	public ResponseEntity<ScheduleResultDto> detailSchedule(@PathVariable("scheduleId") Long scheduleId) {
		ScheduleResultDto scheduleResultDto = scheduleService.detailSchedule(scheduleId);
		
		if("success".equals(scheduleResultDto.getResult())) {
			return ResponseEntity.ok(scheduleResultDto);
		} 
		else {
			return ResponseEntity.internalServerError().body(scheduleResultDto);
		}
	}

	@GetMapping("/schedules/overlap/{eventId}")
	@Operation(summary = "가장 많이 겹치는 시간 조회", description = "이벤트 ID 와 일치하는 이벤트의 스케줄 중 가장 많은 인원이 참여 가능한 스케줄 리스트를 조회합니다.")
	public ResponseEntity<ScheduleResultDto> getMaxOverlapSlots(@PathVariable("eventId") Long eventId) {
		ScheduleResultDto scheduleResultDto = scheduleService.getMaxOverlapSlots(eventId);

		if("success".equals(scheduleResultDto.getResult())) {
			return ResponseEntity.ok(scheduleResultDto);
		}
		else {
			return ResponseEntity.internalServerError().body(scheduleResultDto);
		}
	}
}
