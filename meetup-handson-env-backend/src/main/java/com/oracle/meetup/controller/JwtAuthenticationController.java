package com.oracle.meetup.controller;

import java.util.Map;
import java.util.Optional;

import com.oracle.meetup.auth.config.JwtTokenUtil;
import com.oracle.meetup.dto.JwtRequest;
import com.oracle.meetup.dto.JwtResponse;
import com.oracle.meetup.dto.UserDTO;
import com.oracle.meetup.jpa.RefreshTokenRepository;
import com.oracle.meetup.repository.entity.RefreshTokenDAO;
import com.oracle.meetup.service.JwtUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    // 최초 로그인 시 Access Token과 Refresh Token을 발급 받는다.
    @RequestMapping(value = "/api/auth/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        UserDetails userDetails = null;
        String access_token = null;
        String refresh_token = null;
        String expires_in = null;
        String errorCode = "00"; // success
        String errorMessage = "SUCCESS";
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            access_token = jwtTokenUtil.generateToken(userDetails);
            expires_in = jwtTokenUtil.getExpiresIn(access_token);
            refresh_token = jwtTokenUtil.generateRefreshToken(userDetails);

            if (refresh_token != null && !refresh_token.isEmpty()) {
                // Save refresh_token to Database

                RefreshTokenDAO refreshTokenDAO = new RefreshTokenDAO();
                refreshTokenDAO.setUsername(authenticationRequest.getUsername());
                refreshTokenDAO.setRefreshToken(refresh_token);
                refreshTokenRepository.save(refreshTokenDAO);
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
        return ResponseEntity.ok(new JwtResponse(access_token, refresh_token, expires_in, errorCode, errorMessage));
    }

    // Access Token이 만료되었을 경우 Refresh Token을 활용하여 신규 Access Token과 Refresh Token을 재
    // 발급한다.
    @RequestMapping(value = "/api/auth/token", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationTokenByRefreshToken(@RequestBody Map<String, Object> data)
            throws Exception {
        String access_token = null;
        String refresh_token = null;
        String expires_in = null;
        String errorCode = "00"; // success
        String errorMessage = "SUCCESS";

        String username = (data.get("username") == null ? "" : (String) data.get("username"));
        String refreshToken = (data.get("refresh_token") == null ? "" : (String) data.get("refresh_token"));

        Optional<RefreshTokenDAO> verifiedRefreshTokenDAO = refreshTokenRepository
                .findByUsernameAndRefreshToken(username, refreshToken);

        if (verifiedRefreshTokenDAO.isPresent()) {
            // 토크 생성
            //
            access_token = jwtTokenUtil.generateToken(verifiedRefreshTokenDAO.get().getUsername());
            refresh_token = jwtTokenUtil.generateRefreshToken(verifiedRefreshTokenDAO.get().getUsername());
            expires_in = jwtTokenUtil.getExpiresIn(access_token);
        }

        if (refresh_token != null && !refresh_token.isEmpty()) {
            // Save refresh_token to Database

            RefreshTokenDAO refreshTokenDAO = new RefreshTokenDAO();
            refreshTokenDAO.setUsername(verifiedRefreshTokenDAO.get().getUsername());
            refreshTokenDAO.setRefreshToken(refresh_token);
            refreshTokenRepository.save(refreshTokenDAO);
        }

        return ResponseEntity.ok(new JwtResponse(access_token, refresh_token, expires_in, errorCode, errorMessage));
    }

    // 신규 사용자를 등록한다.
    @RequestMapping(value = "/api/auth/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {

        long userCount = userDetailsService.userCount(user.getUsername());

        if (userCount > 0) {
            throw new Exception("Sorry... Already username taken");
        }
        return ResponseEntity.ok(userDetailsService.save(user));
    }

    // 로그아웃할 경우 서버의 Refresh Token과 클라이언트의 Access Token을 삭제한다.

    // 인증
    private void authenticate(String username, String password) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}