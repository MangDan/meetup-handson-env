package com.oracle.meetup.jpa;

import com.oracle.meetup.repository.entity.HandsOnEnv;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandsOnEnvRepository extends JpaRepository<HandsOnEnv, Integer> {
    public HandsOnEnv findByUsernameAndEmail(String username, String email);
}