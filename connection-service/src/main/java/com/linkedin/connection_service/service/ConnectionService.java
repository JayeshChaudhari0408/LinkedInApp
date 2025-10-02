package com.linkedin.connection_service.service;

import com.linkedin.connection_service.entity.Person;

import java.util.List;

public interface ConnectionService {

    List<Person> getFirstDegreeConnection(Long userId);

}