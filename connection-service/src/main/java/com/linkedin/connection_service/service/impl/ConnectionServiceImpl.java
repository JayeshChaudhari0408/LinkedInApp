package com.linkedin.connection_service.service.impl;

import com.linkedin.connection_service.auth.UserContextHolder;
import com.linkedin.connection_service.entity.Person;
import com.linkedin.connection_service.event.AcceptConnectionRequestEvent;
import com.linkedin.connection_service.event.SendConnectionRequestEvent;
import com.linkedin.connection_service.repository.PersonRepository;
import com.linkedin.connection_service.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final PersonRepository personRepository;
    private final KafkaTemplate<Long, SendConnectionRequestEvent> sendRequestKafkaTemplate;
    private final KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptRequestKafkaTemplate;

    @Override
    public List<Person> getFirstDegreeConnection() {
        Long userId = UserContextHolder.getCurrentUserId();
        return personRepository.getFirstDegreeConnection(userId);
    }

    @Override
    public Boolean sendConnectionRequest(Long receiverId) {
        Long senderId = UserContextHolder.getCurrentUserId();

        if(senderId.equals(receiverId)) {
            throw new RuntimeException("Sender and Receiver are same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId,receiverId);
        if(alreadySentRequest) {
            throw new RuntimeException("Connection request already sent, cannot send again");
        }
        boolean alreadyConnected = personRepository.alreadyConnected(senderId,receiverId);
        if(alreadyConnected) {
            throw new RuntimeException("Already Connected users, cannot add connection request");
        }

        personRepository.addConnectionRequest(senderId,receiverId);
        SendConnectionRequestEvent sendConnectionRequestEvent = SendConnectionRequestEvent.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .build();

        sendRequestKafkaTemplate.send("send-connection-request-topic",sendConnectionRequestEvent);
        return true;
    }

    @Override
    public Boolean acceptConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();
        if(senderId.equals(receiverId)) {
            throw new RuntimeException("Sender and Receiver are same");
        }
        boolean connectionRequestExists = personRepository.connectionRequestExists(senderId,receiverId);
        if(!connectionRequestExists) {
            throw new RuntimeException("No Connection request exists to accept");
        }
        personRepository.acceptConnectionRequest(senderId,receiverId);

        AcceptConnectionRequestEvent acceptConnectionRequestEvent = AcceptConnectionRequestEvent.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .build();

        acceptRequestKafkaTemplate.send("accept-connection-request-topic",acceptConnectionRequestEvent);
        return true;
    }

    @Override
    public Boolean rejectConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();
        if(senderId.equals(receiverId)) {
            throw new RuntimeException("Sender and Receiver are same");
        }
        boolean connectionRequestExists = personRepository.connectionRequestExists(senderId,receiverId);
        if(!connectionRequestExists) {
            throw new RuntimeException("No Connection request exists, cannot reject");
        }
        personRepository.rejectConnectionRequest(senderId,receiverId);
        return true;
    }
}