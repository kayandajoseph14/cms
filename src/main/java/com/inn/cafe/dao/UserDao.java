package com.inn.cafe.dao;

import com.inn.cafe.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository  // Marks this interface as a Spring repository component
public interface UserDao extends JpaRepository<User, Integer> {

    /**
     * Custom method to find a user by their email address.
     * This method is automatically implemented by Spring Data JPA based on the method name convention.
     *
     * @param email The email address used to search for a user.
     * @return The User object matching the provided email address, or null if no match is found.
     */
    User findByEmailId(@Param("email") String email);  // Declares a custom query method to find a user by email
}
