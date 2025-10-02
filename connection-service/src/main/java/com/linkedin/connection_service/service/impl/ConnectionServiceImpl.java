package com.linkedin.connection_service.service.impl;

import com.linkedin.connection_service.entity.Person;
import com.linkedin.connection_service.repository.PersonRepository;
import com.linkedin.connection_service.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final PersonRepository personRepository;

    @Override
    public List<Person> getFirstDegreeConnection(Long userId) {

        return personRepository.getFirstDegreeConnection(userId);
    }
}