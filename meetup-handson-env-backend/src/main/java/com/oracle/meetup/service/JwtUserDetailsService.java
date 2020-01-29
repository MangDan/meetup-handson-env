package com.oracle.meetup.service;

import java.util.ArrayList;

import com.oracle.meetup.dto.UserDTO;
import com.oracle.meetup.jpa.RefreshTokenRepository;
import com.oracle.meetup.jpa.UserRepository;
import com.oracle.meetup.repository.entity.RefreshTokenDAO;
import com.oracle.meetup.repository.entity.UserDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // if ("javainuse".equals(username)) {
        // return new User("javainuse",
        // "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
        // new ArrayList<>());
        // } else {
        // throw new UsernameNotFoundException("User not found with username: " +
        // username);
        // }

        UserDAO user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    public long userCount(String username) {
        return userRepository.countByUsername(username);
    }

    public UserDAO save(UserDTO user) {
        UserDAO newUser = new UserDAO();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userRepository.save(newUser);
    }

    public RefreshTokenDAO saveRefreshToken(RefreshTokenDAO refreshToken) {
        RefreshTokenDAO newRefreshTokenDAO = new RefreshTokenDAO();
        newRefreshTokenDAO.setUsername(refreshToken.getUsername());
        newRefreshTokenDAO.setRefreshToken(refreshToken.getRefreshToken());
        return refreshTokenRepository.save(refreshToken);
    }
}