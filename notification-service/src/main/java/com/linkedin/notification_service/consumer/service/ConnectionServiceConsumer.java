package com.linkedin.notification_service.consumer.service;

import com.linkedin.connection_service.event.AcceptConnectionRequestEvent;
import com.linkedin.connection_service.event.SendConnectionRequestEvent;
import com.linkedin.notification_service.entity.Notification;
import com.linkedin.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionServiceConsumer {

    private final NotificationRepository notificationRepository;


    @KafkaListener(topics = "send-connection-request-topic")
    public void handleSendConnectionRequest(SendConnectionRequestEvent sendConnectionRequestEvent) {
        Long senderId = sendConnectionRequestEvent.getSenderId();
        Long receiverId = sendConnectionRequestEvent.getReceiverId();
        String message = "You have received  a connection request from user with id: "+senderId;

        sendNotification(receiverId,message);
    }

    @KafkaListener(topics = "accept-connection-request-topic")
    public void handleAcceptConnectionRequest(AcceptConnectionRequestEvent acceptConnectionRequestEvent) {
        Long senderId = acceptConnectionRequestEvent.getSenderId();
        Long receiverId = acceptConnectionRequestEvent.getReceiverId();
        String message = "Your connection request has been accepted by user with id: "+receiverId;

        sendNotification(senderId,message);
    }


    public void sendNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}
