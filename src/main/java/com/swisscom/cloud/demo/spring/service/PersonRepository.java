/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swisscom.cloud.demo.spring.model.Person;

/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
public interface PersonRepository extends JpaRepository<Person, String> {

}
