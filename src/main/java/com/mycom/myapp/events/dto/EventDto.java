package com.mycom.myapp.events.dto;

import java.time.LocalDate;
import java.util.List;

import com.mycom.myapp.events.dto.type.EventStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long eventId;
    private String title;
    private Long ownerId; // 이벤트 오너
    private List<LocalDate> eventDates; // 이벤트 날짜 리스트
    private List<Long> userIds; // 이벤트에 참여 중인 사용자 리스트
    private EventStatus status;
}
