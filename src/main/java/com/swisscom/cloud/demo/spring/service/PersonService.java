/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.service;

import com.swisscom.cloud.demo.spring.model.Person;

/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
public interface PersonService {

    Person getPerson(String id);

    Iterable<Person> listPerson();

    void addPerson(Person person);

    void updatePerson(Person person);

    void deletePerson(Person person);

}
