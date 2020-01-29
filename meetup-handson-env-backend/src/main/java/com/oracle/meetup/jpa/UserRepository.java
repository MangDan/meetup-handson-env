package com.oracle.meetup.jpa;

import com.oracle.meetup.repository.entity.UserDAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, Integer> {
    UserDAO findByUsername(String username);

    long countByUsername(String username);
}