package com.mycom.myapp.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

	private Long userId;
	
	private Long eventId;
	
	private String title;
	
}
