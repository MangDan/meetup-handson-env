package com.oracle.meetup.auth.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;
    // public static final long JWT_TOKEN_VALIDITY = 1 * (60 * 60); // 1 hours
    public static final long JWT_TOKEN_VALIDITY = 60; // 1 min
    public static final long JWT_REFRESH_TOKEN_VALIDITY = 14 * (60 * 60 * 24); // 2 weeks

    @Value("${jwt.secret}")
    private String secret;

    // retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // retrieve expiration date from jwt token
    public String getExpiresIn(String token) {
        System.out.println(Math.abs(getClaimFromToken(token, Claims::getExpiration).getTime()
                - new Date(System.currentTimeMillis()).getTime()));
        return Long.toString(Math.abs(getClaimFromToken(token, Claims::getExpiration).getTime()
                - new Date(System.currentTimeMillis()).getTime()) / 1000);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // generate token for user
    public String generateToken(UserDetails userDetails) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // generate token for user
    public String generateToken(String username) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
    }

    // generate refresh token for user
    public String generateRefreshToken(UserDetails userDetails) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateRefreshToken(claims, userDetails.getUsername());
    }

    // generate refresh token for user
    public String generateRefreshToken(String username) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateRefreshToken(claims, username);
    }

    // while creating the token -
    // 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
    // 2. Sign the JWT using the HS512 algorithm and secret key.
    // 3. According to JWS Compact
    // Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    // compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) throws Exception {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) throws Exception {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    // validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        // InvalidTokenException
        final String username = getUsernameFromToken(token);

        // if (!username.equals(userDetails.getUsername()))
        // throw new InvalidClaimException("");

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}