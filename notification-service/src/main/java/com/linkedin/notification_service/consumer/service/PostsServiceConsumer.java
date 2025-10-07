package com.linkedin.notification_service.consumer.service;

import com.linkedin.notification_service.clients.ConnectionClient;
import com.linkedin.notification_service.dto.PersonDto;
import com.linkedin.notification_service.entity.Notification;
import com.linkedin.notification_service.repository.NotificationRepository;
import com.linkedin.posts_service.event.PostCreatedEvent;
import com.linkedin.posts_service.event.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsServiceConsumer {

    private final ConnectionClient connectionClient;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "post-created-topic")
    public void handlePostCreated(PostCreatedEvent postCreatedEvent) {
        List<PersonDto> connections = connectionClient.getFirstConnections(postCreatedEvent.getCreatorId());

        for(PersonDto connection: connections) {
            sendNotification(connection.getUserId(),
                    "YourConnection "+postCreatedEvent.getCreatorId()+" has created a post!");
        }
    }

    @KafkaListener(topics = "post-liked-topic")
    public void handlePostLiked(PostLikeEvent postLikeEvent) {
        Long creatorId = postLikeEvent.getCreatorId();
        String message = String.format("Your post, %d has been liked by %d"
                ,postLikeEvent.getPostId(),postLikeEvent.getLikedByUserId());
        sendNotification(creatorId,message);
    }

    public void sendNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}
