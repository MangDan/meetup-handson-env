package com.oracle.meetup.controller;

import java.util.List;
import java.util.Optional;

import com.oracle.meetup.jpa.HandsOnEnvRepository;
import com.oracle.meetup.repository.entity.HandsOnEnv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@CrossOrigin("*")
public class HandsOnEnvController {

    @Autowired
    HandsOnEnvRepository handsOnEnvRepository;

    Logger logger = LoggerFactory.getLogger(HandsOnEnvController.class);

    @RequestMapping(value = "/api/v1/meetup/envs", method = RequestMethod.GET)
    public List<HandsOnEnv> envs(Sort sort) throws Exception {
        List<HandsOnEnv> handsOnEnv = handsOnEnvRepository.findAll(sort);
        return handsOnEnv;
    }

    @PostMapping(value = "/api/v1/meetup/env")
    public HandsOnEnv updateEnv(@RequestBody HandsOnEnv handsOnEnv) throws Exception {
        Optional<HandsOnEnv> handsOnEnvById = handsOnEnvRepository.findById(handsOnEnv.getNum());

        if (handsOnEnvById.isPresent()) {
            if (handsOnEnvById.get().getEmail() == null || handsOnEnvById.get().getEmail().isEmpty()) {
                return handsOnEnvRepository.save(handsOnEnv);
            } else {
                throw new Exception(handsOnEnvById.get().getUsername() + "님이 이미 등록하였습니다.");
            }
        } else {
            throw new Exception("Please submit with other email.");
        }
    }

    @PostMapping(value = "/api/v1/admin/meetup/env/reset")
    public HandsOnEnv resetEnv(@RequestBody HandsOnEnv handsOnEnv) throws Exception {
        return handsOnEnvRepository.save(handsOnEnv);
    }
    // @PostMapping(value = "/api/v1/admin/meetup/env/remove")
    // public void removeEnv(@RequestBody HandsOnEnv handsOnEnv) throws Exception {
    // handsOnEnvRepository.deleteById(handsOnEnv.getNum());
    // }

    @RequestMapping(value = "/api/v1/admin/meetup/env/{num}", method = RequestMethod.DELETE)
    public void removeEnv(@PathVariable("num") int num) throws Exception {
        handsOnEnvRepository.deleteById(num);
    }

    @RequestMapping(value = "/api/v1/meetup/env/{num}", method = RequestMethod.GET)
    public Optional<HandsOnEnv> getEnvById(@PathVariable("num") int num) throws Exception {
        return handsOnEnvRepository.findById(num);
    }

    @RequestMapping(value = "/api/v1/meetup/env", method = RequestMethod.GET)
    public HandsOnEnv getEnv(@RequestParam("username") String username, @RequestParam("email") String email)
            throws Exception {
        return handsOnEnvRepository.findByUsernameAndEmail(username, email);
    }

    public static void main(String[] args) {
        SpringApplication.run(HandsOnEnvController.class, args);
    }
}