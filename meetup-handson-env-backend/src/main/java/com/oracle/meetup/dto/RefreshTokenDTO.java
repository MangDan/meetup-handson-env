package com.oracle.meetup.dto;

public class RefreshTokenDTO {
    private String username;
    private String refresh_token;

    public RefreshTokenDTO(String username, String refresh_token) {
        this.username = username;
        this.refresh_token = refresh_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}