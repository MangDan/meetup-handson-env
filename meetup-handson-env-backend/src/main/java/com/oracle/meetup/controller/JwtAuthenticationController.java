package com.oracle.meetup.controller;

import java.util.Map;
import java.util.Optional;

import com.oracle.meetup.auth.config.JwtTokenUtil;
import com.oracle.meetup.dto.JwtResponse;
import com.oracle.meetup.dto.UserDTO;
import com.oracle.meetup.jpa.RefreshTokenRepository;
import com.oracle.meetup.repository.entity.RefreshTokenDAO;
import com.oracle.meetup.service.JwtAuthenticationService;
import com.oracle.meetup.service.JwtUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /**
     * Issue or Todo Refresh Token의 유효기간이 2일이지만, 만일 사용자가 2일동안 브라우저를 닫지 않고 대기하고 있는
     * 상태라면,
     * 
     */
    // 최초 로그인 시 Access Token과 Refresh Token을 발급 받는다.
    @RequestMapping(value = "/api/auth/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDTO authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        return ResponseEntity.ok(jwtAuthenticationService.getTokens(authenticationRequest.getUsername()));
    }

    // Access Token이 만료되었을 경우 Refresh Token을 활용하여 신규 Access Token과 Refresh Token을
    // 재발급한다.
    @RequestMapping(value = "/api/auth/token", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationTokenByRefreshToken(@RequestBody Map<String, Object> data)
            throws Exception {

        String username = (data.get("username") == null ? "" : (String) data.get("username"));
        String refreshToken = (data.get("refresh_token") == null ? "" : (String) data.get("refresh_token"));

        Optional<RefreshTokenDAO> verifiedRefreshTokenDAO = refreshTokenRepository
                .findByUsernameAndRefreshToken(username, refreshToken);

        if (verifiedRefreshTokenDAO.isPresent()) {
            return ResponseEntity.ok(jwtAuthenticationService.getTokens(verifiedRefreshTokenDAO.get().getUsername()));
        } else {
            return ResponseEntity.ok(new JwtResponse(null, null, null, "502", "INVALID_REFRESH_TOKEN"));
        }
    }

    @RequestMapping(value = "/api/auth/claims", method = RequestMethod.POST)
    public ResponseEntity<?> getAllClaimsFromToken(@RequestHeader(value = "X-Authorization") String access_token,
            @RequestBody Map<String, Object> data) throws Exception {
        String username = (data.get("username") == null ? "" : (String) data.get("username"));

        if (access_token.startsWith("Bearer")) {
            access_token = access_token.substring(7);
        }

        // Claims claims = jwtTokenUtil.getAllClaimsFromToken(access_token);

        if (!username.equals(""))
            return ResponseEntity.ok(jwtTokenUtil.getAllClaimsFromToken(access_token));
        else
            throw new UsernameNotFoundException("Username not found!!!");
    }

    // 신규 사용자를 등록한다.
    @RequestMapping(value = "/api/auth/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {

        long userCount = userDetailsService.userCount(user.getUsername());

        if (userCount > 0) {
            throw new Exception("Sorry... Already username taken!");
        }

        userDetailsService.save(user);
        return ResponseEntity.ok(jwtAuthenticationService.getTokens(user.getUsername()));
    }

    // 인증
    private void authenticate(String username, String password) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}