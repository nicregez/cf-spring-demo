/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swisscom.cloud.demo.spring.model.Person;


/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Person getPerson(String id) {
        return personRepository.findOne(id);
    }

    @Override
    public Iterable<Person> listPerson() {
        return personRepository.findAll();
    }

    @Override
    public void addPerson(Person person) {
        person.setId(UUID.randomUUID().toString());
        personRepository.save(person);
    }

    @Override
    public void updatePerson(Person person) {
        personRepository.save(person);
    }

    @Override
    public void deletePerson(Person person) {
        personRepository.delete(person);
    }

}
