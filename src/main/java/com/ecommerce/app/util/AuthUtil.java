package com.ecommerce.app.util;

import com.ecommerce.app.modal.User;
import com.ecommerce.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    public boolean isUserLoggedIn() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return true;
    }

    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return user.getEmail();
    }

    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return user;
    }

    public String loggedInUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        return authentication.getName();

    }

    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return user.getUserId();
    }

}
