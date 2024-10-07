package com.inn.cafe.restImpI;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.rest.UserRest;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Implementation of UserRest interface for handling user-related operations.
 */
@RestController
public class UserRestImpl implements UserRest {

    // Autowiring the UserService to handle user operations
    @Autowired
    UserService userService;

    /**
     * Handles user sign-up requests.
     *
     * @param requestMap A map containing user sign-up details.
     * @return A ResponseEntity containing the response message and HTTP status.
     */
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {

        try {
            // Delegating the sign-up process to the userService
            return userService.signUp(requestMap);
        } catch (Exception ex) {
            // Logging the exception stack trace for debugging
            ex.printStackTrace();
        }

        // Returning a generic error response in case of exceptions
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try{
            // Delegating the login process to the userService
            return userService.login(requestMap);
        }catch (Exception ex){
            // Logging the exception stack trace for debugging
            ex.printStackTrace();
        }
        // Returning a generic error response in case of exceptions
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
