package com.mycom.myapp.events.controller;

import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.events.dto.EventResultDto;
import com.mycom.myapp.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    @PostMapping("/events")
    public ResponseEntity<EventResultDto> createEvent(EventDto eventDto) {
        EventResultDto result = eventService.createEvent(eventDto);

        return switch (result.getResult()) {
            case "success" -> ResponseEntity.status(HttpStatus.CREATED).body(result);
            case "invalid_input" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        };
    }
}
