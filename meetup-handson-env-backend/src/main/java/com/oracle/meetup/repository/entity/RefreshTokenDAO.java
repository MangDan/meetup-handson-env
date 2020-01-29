package com.oracle.meetup.repository.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "JWT_REFRESH_TOKEN")
public class RefreshTokenDAO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3423961242822117508L;

    @Id
    @Column(name = "USERNAME")
    private String username;

    @JsonIgnore
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}