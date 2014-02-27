/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.service;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.swisscom.cloud.demo.spring.model.Person;

/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
public interface PersonRepository extends Repository<Person, String> {

    List<Person> findAll();

    Person findOne(String id);

    <S extends Person> S save(Person person);

    void delete(Person entity);

}
