package com.mycom.myapp.notifications.dto;

import lombok.Data;

@Data
public class UserEventDto {
	
	private Long userId;
	
	private Long eventId;
	
	private Integer isSent;
	
}
