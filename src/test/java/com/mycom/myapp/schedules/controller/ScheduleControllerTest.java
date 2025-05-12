package com.mycom.myapp.schedules.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mycom.myapp.schedules.dto.ScheduleDto;
import com.mycom.myapp.schedules.dto.ScheduleResultDto;
import com.mycom.myapp.schedules.service.ScheduleService;

@WithMockUser
@WebMvcTest(ScheduleController.class)
@AutoConfigureMockMvc
public class ScheduleControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private ScheduleService scheduleService;
	
	@Test
	void insertSchedule_Success() throws Exception {
		ScheduleResultDto resultDto = new ScheduleResultDto();
		resultDto.setResult("success");
		
		when(scheduleService.insertSchedule(any(ScheduleDto.class))).thenReturn(resultDto);
		
		mockMvc.perform(post("/api/schedules")
			    .with(csrf())
			    .param("scheduleId", "1")
			    .param("userId", "1")
			    .param("eventId", "205")
			    .param("startTime", "2025-05-15T13:00:00")
			    .param("endTime", "2025-05-15T15:00:00"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value("success"));
		
	}
	
	@Test
	void insertSchedule_Fail() throws Exception {
		ScheduleResultDto resultDto = new ScheduleResultDto();
		resultDto.setResult("fail");
		
		when(scheduleService.insertSchedule(any(ScheduleDto.class)))
				.thenReturn(resultDto);
		
		mockMvc.perform(post("/api/schedules")
			    .with(csrf())
			    .param("scheduleId", "1")
			    .param("userId", "1")
			    .param("eventId", "205")
			    .param("startTime", "2025-05-15T13:00:00")
			    .param("endTime", "2025-05-15T15:00:00"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.result").value("fail"));
		
	}
	
	@Test
	void listSchedule_Success() throws Exception {
		ScheduleDto schedule1 = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.eventId(205L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		ScheduleDto schedule2 = ScheduleDto.builder()
				.scheduleId(2L)
				.userId(1L)
				.eventId(205L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		ScheduleResultDto resultDto = new ScheduleResultDto();
		resultDto.setScheduleDtoList(List.of(schedule1, schedule2));
		resultDto.setResult("success");
		
		when(scheduleService.listSchedule(205L)).thenReturn(resultDto);
		
		mockMvc.perform(get("/api/event/205/schedules")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value("success"));
		
	}
	
	@Test
	void listSchedule_Fail() throws Exception {		
		ScheduleResultDto resultDto = new ScheduleResultDto();
		resultDto.setResult("fail");
		
		when(scheduleService.listSchedule(205L)).thenReturn(resultDto);
		
		mockMvc.perform(get("/api/event/205/schedules")
				.with(csrf()))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.result").value("fail"));
		
	}
	
	@Test
	void detailSchedule_Success() throws Exception {
		ScheduleDto scheduleDto = ScheduleDto.builder()
				.scheduleId(1L)
				.userId(1L)
				.eventId(205L)
				.startTime(LocalDateTime.of(2025, 5, 15, 13, 00, 00))
				.endTime(LocalDateTime.of(2025, 5, 15, 15, 00, 00))
				.build();
		
		ScheduleResultDto resultDto = new ScheduleResultDto();
		resultDto.setResult("success");
		resultDto.setScheduleDto(scheduleDto);
		
		when(scheduleService.detailSchedule(1L)).thenReturn(resultDto);
		
		mockMvc.perform(get("/api/schedules/1")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value("success"));
		
	}
	
	@Test
	void detailSchedule_Fail() throws Exception {
		ScheduleResultDto resultDto = new ScheduleResultDto();
		resultDto.setResult("fail");
		
		when(scheduleService.detailSchedule(1L)).thenReturn(resultDto);
		
		mockMvc.perform(get("/api/schedules/1")
				.with(csrf()))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.result").value("fail"));
		
	}
}
