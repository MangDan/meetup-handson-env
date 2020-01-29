package com.oracle.meetup.dto;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String access_token;
    private final String refresh_token;
    private final String expire_in;
    private final String errorCode;
    private final String errorMessage;

    public JwtResponse(String access_token, String refresh_token, String expire_in, String errorCode,
            String errorMessage) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expire_in = expire_in;
        this.errorCode = errorCode; // 00 성공, 107 파라미터 오류, 502 accessToken 발급 오류, 99 알수 없는 오류
        this.errorMessage = errorMessage; // 00 성공, 107 파라미터 오류, 502 accessToken 발급 오류, 99 알수 없는 오류
    }

    public String getAccess_token() {
        return this.access_token;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public String getExpires_in() {
        return this.expire_in;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
