package com.oracle.meetup.jpa;

import java.util.Optional;

import com.oracle.meetup.repository.entity.RefreshTokenDAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenDAO, String> {
    Optional<RefreshTokenDAO> findByUsernameAndRefreshToken(String username, String refreshToken);
}