package com.inn.cafe.JWT;

import com.inn.cafe.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service  // Marks this class as a Spring Service bean, making it available for dependency injection
public class CustomerUsersDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;  // Automatically injects an instance of UserDao to interact with the database

    private com.inn.cafe.POJO.User userDetail;  // Stores the details of the user retrieved from the database

    // This method is invoked by Spring Security during the login process.
    // The username (which could be an email) is used to fetch user details from the database.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log username
        log.info("Inside the loadUserByUsername {}", username);
        // Fetch user details from the database using the provided email (username)
        userDetail = userDao.findByEmailId(username);

        // If the user is found, return a Spring Security UserDetails object containing email and password
        if (!Objects.isNull(userDetail))
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        else
            // If the user is not found, throw an exception
            throw new UsernameNotFoundException("User not found.");
    }

    // This method is a custom getter to access the user details retrieved during the authentication process
    public com.inn.cafe.POJO.User getUserDetail() {
        return userDetail;
    }
}
