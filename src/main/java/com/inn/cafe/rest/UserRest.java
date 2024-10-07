package com.inn.cafe.rest;  // Package declaration for REST controllers

import org.springframework.http.ResponseEntity;  // Importing ResponseEntity for handling HTTP responses
import org.springframework.web.bind.annotation.PostMapping;  // Importing annotation for handling POST requests
import org.springframework.web.bind.annotation.RequestBody;  // Importing annotation for binding the request body to a method parameter
import org.springframework.web.bind.annotation.RequestMapping;  // Importing annotation for mapping web requests

import java.util.Map;  // Importing Map to handle the request data as key-value pairs

// This annotation maps requests to the "/user" path, making this interface a REST controller for user-related operations.
@RequestMapping(path = "/user")
public interface UserRest {

    // This method handles POST requests to "/user/signup"
    @PostMapping(path = "/signup")
    // Method signature for user signup, accepting a Map as the request body
    public ResponseEntity<String> signUp(
            @RequestBody(required = true) Map<String, String> requestMap  // Maps the JSON body of the request to a Map
    );

    @PostMapping(path = "/login")
    // Method signature for user login, accepting a Map as the request body
    public ResponseEntity<String> login(
            @RequestBody(required = true) Map<String, String> requestMap // Maps the JSON of the request to a Map
    );
}
