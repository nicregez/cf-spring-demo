/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.swisscom.cloud.demo.spring.model.Person;
import com.swisscom.cloud.demo.spring.service.PersonService;

/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
@Controller
public class PersonController {

    @Autowired
    private PersonService svc;

    @RequestMapping(value = "/person", method = RequestMethod.GET)
    public String getPersonList(ModelMap model) {
        model.addAttribute("personList", svc.listPerson());
        return "person-list";
    }

    @RequestMapping(value = "/person/save", method = RequestMethod.POST)
    public View createPerson(@ModelAttribute Person person, ModelMap model) {
        if (StringUtils.hasText(person.getId())) {
            svc.updatePerson(person);
        } else {
            svc.addPerson(person);
        }
        return new RedirectView("/person", true);
    }

    @RequestMapping(value = "/person/delete", method = RequestMethod.GET)
    public View deletePerson(@ModelAttribute Person person, ModelMap model) {
        svc.deletePerson(person);
        return new RedirectView("/person", true);
    }

}
