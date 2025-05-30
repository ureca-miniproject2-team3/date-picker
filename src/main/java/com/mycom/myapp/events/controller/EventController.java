package com.mycom.myapp.events.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.dto.TimelineDto;
import com.mycom.myapp.events.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Event", description = "이벤트 처리 관련 API")

public class EventController {

    private static final Map<String, HttpStatus> STATUS_MAP = Map.of(
            "success", HttpStatus.OK,
            "not found", HttpStatus.BAD_REQUEST,
            "forbidden", HttpStatus.FORBIDDEN
    );

    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    @Operation(summary = "이벤트 리스트 조회", description = "사용자가 참여 중인 이벤트 리스트를 조회합니다.")
    public ResponseEntity<EventResultDto> listEvent(@PathVariable("userId") Long userId) {

        return createResponse(eventService.listEvent(userId));
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "이벤트 상세 조회", description = "이벤트 상세 정보를 조회합니다.")
    public ResponseEntity<EventResultDto> detailEvent(@PathVariable("eventId") Long eventId) {

        return createResponse(eventService.detailEvent(eventId));
    }

    @PostMapping("/events")
    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    public ResponseEntity<EventResultDto> createEvent(EventDto eventDto) {

        return createResponse(eventService.createEvent(eventDto));
    }

    @PutMapping("/events")
    @Operation(summary = "이벤트 수정", description = "이벤트 제목을 수정하고, 날짜를 추가할 수 있습니다.")
    public ResponseEntity<EventResultDto> updateEvent(EventDto eventDto) {

        return createResponse(eventService.updateEvent(eventDto));
    }

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "이벤트 삭제", description = "이벤트를 삭제합니다.")
    public ResponseEntity<EventResultDto> deleteEvent(@PathVariable("eventId") Long eventId, @RequestParam("userId") Long userId) {

        return createResponse(eventService.deleteEvent(eventId, userId));
    }

    @PostMapping("/events/invite")
    @Operation(summary = "이벤트에 사용자 초대", description = "이벤트 생성자가 이벤트에 다른 사용자를 초대할 수 있습니다.")
    public ResponseEntity<EventResultDto> inviteUserToEvent(@RequestParam("inviterId") Long inviterId,@RequestParam("eventId") Long eventId,@RequestParam("invitedIds") List<Long> invitedIds) {

        return createResponse(eventService.inviteUserToEvent(inviterId, eventId, invitedIds));
    }
    
    @PutMapping("/events/check")
    @Operation(summary = "이벤트 확정", description = "이벤트를 확정(CHECKED) 상태로 바꾸고, 확정된 타임라인을 저장합니다.")
    public ResponseEntity<EventResultDto> checkEvent(@RequestParam("userId") Long userId, TimelineDto timelineDto) {
    	
    	return createResponse(eventService.checkEvent(userId, timelineDto));
    }

    @PutMapping("/events/status")
    @Operation(summary = "이벤트 상태 일괄 업데이트", description = "날짜가 지난 이벤트에 대해 확정 -> 완료, 미확정 -> 만료로 이벤트 상태를 수정합니다.")
    public ResponseEntity<EventResultDto> updateEventStatus() {

        return createResponse(eventService.updateEventStatus());
    }

    private ResponseEntity<EventResultDto> createResponse(EventResultDto result) {
        String resultStatus = result.getResult();
        HttpStatus status = STATUS_MAP.getOrDefault(resultStatus, HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(status).body(result);
    }

}
