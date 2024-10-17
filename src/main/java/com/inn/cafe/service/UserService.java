package com.inn.cafe.service;

import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    // Method to handle user sign-up request with input as a map of request parameters
    public ResponseEntity<String> signUp(Map<String, String> requestMp);

    // Method to handle user login, also accepting a map of parameters and returning a ResponseEntity
    ResponseEntity<String> login(Map<String, String> requestMap);

    // Method to retrieve all users, returns a list of UserWrapper inside a ResponseEntity
    ResponseEntity<List<UserWrapper>> getAllUser();

    // Method to update user details, accepts a map of request parameters and returns a ResponseEntity
    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String> checkToken();

    ResponseEntity<String> changePassword(Map<String, String> requestMap);

    ResponseEntity<String> forgotPassword(Map<String, String> requestMap);
}
