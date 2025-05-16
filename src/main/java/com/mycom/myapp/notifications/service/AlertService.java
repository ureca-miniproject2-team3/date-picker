package com.mycom.myapp.notifications.service;

public interface AlertService {

	void sendNotifications(Long userId, Long eventId, String title);
	
	void sendUnsentNotifications(Long userId);
	
}
