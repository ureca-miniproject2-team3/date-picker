package com.mycom.myapp.events.controller;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Event", description = "이벤트 처리 관련 API")

public class EventController {

    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    @Operation(summary = "이벤트 리스트 조회", description = "사용자가 참여 중인 이벤트 리스트를 조회합니다.")
    public ResponseEntity<EventResultDto> listEvent(@PathVariable Long userId) {
        EventResultDto result = eventService.listEvent(userId);

        if (result.getResult().equals("success")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "이벤트 상세 조회", description = "이벤트 상세 정보를 조회합니다.")
    public ResponseEntity<EventResultDto> detailEvent(@PathVariable Long eventId) {
        EventResultDto result = eventService.detailEvent(eventId);

        if (result.getResult().equals("success")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } else if (result.getResult().equals("not found")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/events")
    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    public ResponseEntity<EventResultDto> createEvent(@RequestBody EventDto eventDto) {
        EventResultDto result = eventService.createEvent(eventDto);

        if (result.getResult().equals("success")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PutMapping("/events")
    @Operation(summary = "이벤트 수정", description = "이벤트 제목을 수정하고, 날짜를 추가할 수 있습니다.")
    public ResponseEntity<EventResultDto> updateEvent(@RequestBody EventDto eventDto) {
        EventResultDto result = eventService.updateEvent(eventDto);

        if (result.getResult().equals("success")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "이벤트 삭제", description = "이벤트를 삭제합니다.")
    public ResponseEntity<EventResultDto> deleteEvent(@PathVariable Long eventId) {
        EventResultDto result = eventService.deleteEvent(eventId);

        if (result.getResult().equals("success")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/events/invite")
    @Operation(summary = "이벤트에 사용자 초대", description = "이벤트 생성자가 이벤트에 다른 사용자를 초대할 수 있습니다.")
    public ResponseEntity<EventResultDto> inviteUserToEvent(Long inviterId, Long eventId, List<Long> invitedIds) {
        EventResultDto result = eventService.inviteUserToEvent(inviterId, eventId, invitedIds);

        if (result.getResult().equals("success")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } else if (result.getResult().equals("forbidden")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
