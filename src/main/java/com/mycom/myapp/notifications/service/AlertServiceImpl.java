package com.mycom.myapp.notifications.service;

import com.mycom.myapp.notifications.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.events.dao.EventDao;
import com.mycom.myapp.events.dto.EventDto;
import com.mycom.myapp.notifications.dto.NotificationDto;
import com.mycom.myapp.notifications.dto.UserEventDto;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {
	
	private final EventDao eventDao;
	
	private final SimpMessagingTemplate messagingTemplate;
	
	 @Override
    public void sendNotifications(Long userId, Long eventId, String title) {
        NotificationDto notificationDto = new NotificationDto(userId, eventId, title);
        messagingTemplate.convertAndSend("/topic/user/" + userId, notificationDto);
    }

	@Override
	@Transactional
	public void sendUnsentNotifications(Long userId) {
		List<NotificationDto> notifications = new ArrayList<>();
		
		List<UserEventDto> userEvents = eventDao.listUnsentUserEvent(userId);
		
		for(UserEventDto userEvent : userEvents) {
			EventDto eventDto = eventDao.detailEvent(userEvent.getEventId());
			
			Long ownerId = eventDto.getOwnerId();
			String title = eventDto.getTitle();
			
			if(!Objects.equals(ownerId, userEvent.getUserId())) {
				NotificationDto notificationDto = NotificationDto.builder()
						.userId(userId)
						.eventId(userEvent.getEventId())
						.title(title)
						.build();
				
				notifications.add(notificationDto);
			}
		}
		
		for(NotificationDto notification : notifications) {
			messagingTemplate.convertAndSend("/topic/user/" + userId, notification);
			
			eventDao.updateUnsentUserEvent(notification.getUserId(), notification.getEventId());
		}
	}
}
