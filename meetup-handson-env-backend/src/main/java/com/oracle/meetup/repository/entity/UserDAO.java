package com.oracle.meetup.repository.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "USERS")
public class UserDAO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3423961242822117508L;

    @Id
    @Column(name = "USERNAME", updatable = false)
    private String username;

    @JsonIgnore
    @Column(name = "PASSWORD")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}