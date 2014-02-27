/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swisscom.cloud.demo.spring.model.Person;


/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
@Transactional
@Component
public class PersonRepositoryImpl implements PersonRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Person> findAll() {
        Query q = em.createQuery("SELECT p FROM Person p");
        @SuppressWarnings("unchecked")
        List<Person> result = q.getResultList();
        return result;
    }

    @Override
    public Person findOne(String id) {
        Query q = em.createQuery("SELECT p FROM Person p WHERE p.id = :id");
        q.setParameter("id", id);
        return (Person) q.getSingleResult();
    }

    @Override
    public <S extends Person> S save(Person person) {
        em.merge(person);
        @SuppressWarnings("unchecked")
        S result = (S) person;
        return result;
    }

    @Override
    public void delete(Person person) {
        Person loaded = findOne(person.getId());
        if (loaded != null) {
            em.remove(loaded);
        }
    }

}
