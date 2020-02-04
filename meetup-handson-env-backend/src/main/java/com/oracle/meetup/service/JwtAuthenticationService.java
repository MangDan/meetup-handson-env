package com.oracle.meetup.service;

import com.oracle.meetup.auth.config.JwtTokenUtil;
import com.oracle.meetup.dto.JwtResponse;
import com.oracle.meetup.jpa.RefreshTokenRepository;
import com.oracle.meetup.repository.entity.RefreshTokenDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthenticationService {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public JwtResponse getTokens(String username) throws UsernameNotFoundException {
        String access_token = null;
        String refresh_token = null;
        String expires_in = null;
        String errorCode = "00"; // success
        String errorMessage = "SUCCESS";

        try {
            access_token = jwtTokenUtil.generateToken(username);
            refresh_token = jwtTokenUtil.generateRefreshToken(username);
            expires_in = jwtTokenUtil.getExpiresIn(access_token);

            if (refresh_token != null && !refresh_token.isEmpty()) {
                // Save refresh_token to Database

                RefreshTokenDAO refreshTokenDAO = new RefreshTokenDAO();
                refreshTokenDAO.setUsername(username);
                refreshTokenDAO.setRefreshToken(refresh_token);
                refreshTokenRepository.save(refreshTokenDAO); // Save refresh token
            } else {
                throw new BadCredentialsException("INVALID_CREDENTIALS");
            }
        } catch (DisabledException e) {
            errorCode = "107";
            errorMessage = "USER_DISABLED";
        } catch (BadCredentialsException e) {
            errorCode = "107";
            errorMessage = "INVALID_CREDENTIALS";
        } catch (UsernameNotFoundException unfe) {
            errorCode = "99";
            errorMessage = unfe.getMessage();
        } catch (Exception e) {
            errorCode = "502";
            errorMessage = e.getMessage();
        }
        // 00 성공, 107 파라미터 오류, 502 accessToken 발급 오류, 99 알수 없는 오류
        return new JwtResponse(access_token, refresh_token, expires_in, errorCode, errorMessage);
    }

}