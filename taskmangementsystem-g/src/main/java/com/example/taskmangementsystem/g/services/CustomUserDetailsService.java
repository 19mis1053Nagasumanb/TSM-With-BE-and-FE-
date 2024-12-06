package com.example.taskmangementsystem.g.services;

import com.example.taskmangementsystem.g.securitys.CustomUserDetails;
import com.example.taskmangementsystem.g.filters.JwtRequestFilter;
import com.example.taskmangementsystem.g.models.App_User;
import com.example.taskmangementsystem.g.repositorys.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService  implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        logger.debug("Searching for user with username: {}", username);

        log.info("the logged in user is : {}", username);

//        App_User appUser = userRepository.findByUsernameIgnoreCase(username);
        App_User appUser = userRepository.findByEmailIgnoreCase(username);

        if (appUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(appUser);
    }
}
