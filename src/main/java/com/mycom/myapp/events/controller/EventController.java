package com.mycom.myapp.events.controller;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Event", description = "이벤트 처리 관련 API")

public class EventController {

    private final EventService eventService;

    @PostMapping("/events")
    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    public ResponseEntity<EventResultDto> createEvent(EventDto eventDto) {
        EventResultDto result = eventService.createEvent(eventDto);

        if (result.getResult().equals("success")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PutMapping("/events")
    @Operation(summary = "이벤트 수정", description = "이벤트 제목을 수정하고, 날짜를 추가할 수 있습니다.")
    public ResponseEntity<EventResultDto> updateEvent(EventDto eventDto) {
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
}
