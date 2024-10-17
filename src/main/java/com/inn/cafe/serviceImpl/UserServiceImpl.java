package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUsersDetailsService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.User;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.utils.PasswordUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;  // Injecting the UserDao repository for database operations

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    /**
     * Handles the user signup process.
     * This method takes a map of user details and tries to register a new user if valid.
     *
     * @param requestMap The map containing user details (name, email, etc.)
     * @return A ResponseEntity with a success or failure message and the corresponding HTTP status
     */
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);  // Log the request details for debugging
        try {
            // Check if the input data in the request map is valid
            if (validateSignUpMap(requestMap)) {

                // Extract email and password from requestMap
                String email = (String) requestMap.get("email");
                String rawPassword = (String) requestMap.get("password");

                // Check if the email already exists in the database
                User user = userDao.findByEmailId(email);

                // If the user doesn't exist, proceed with saving the new user
                if (Objects.isNull(user)) {
                    // Create a new user object from requestMap
                    User newUser = getUserFromMap(requestMap);

                    // Encrypt the password before setting it to the user
                    newUser.setPassword(bCryptPasswordEncoder.encode(rawPassword));

                    // Save the new user to the database
                    userDao.save(newUser);

                    // Return success message
                    return CafeUtils.getResponseEntity(CafeConstants.SUCCESSFUL_REGISTERED, HttpStatus.OK);  // Return success message
                } else {
                    // If the email already exists, return a bad request response
                    return CafeUtils.getResponseEntity(CafeConstants.EMAIL_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
                }
            } else {
                // If the data is invalid, return a bad request response
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            // Handle any exceptions that occur during signup
            ex.printStackTrace();
        }

        // If something goes wrong, return a generic error response
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Validates the signup request by checking if all required fields are present in the request map.
     *
     * @param requestMap The map containing user data (name, contactNumber, email, password)
     * @return true if all fields are present, false otherwise
     */
    private boolean validateSignUpMap(Map<String, String> requestMap) {
        // Check if all required fields (name, contactNumber, email, password) are provided
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")) {
            return true;  // Data is valid
        }
        return false;  // Missing required fields, return invalid
    }

    /**
     * Helper method to map the request data into a User object.
     *
     * @param requestMap The map containing user details from the signup request
     * @return A User object populated with the request data
     */
    private User getUserFromMap(Map<String, String> requestMap) {
        // Create a new User object and populate it with the request data
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");  // Default status set to 'false' (not active)
        user.setRole("user");  // Default role set to 'user'

        return user;  // Return the newly created user object
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        // Log the entry into the login method
        log.info("Inside login");

        try {
            // Extract email and password from the request map
            String email = requestMap.get("email");
            String rawPassword = requestMap.get("password");

            // Retrieve the user by email
            User user = userDao.findByEmailId(email);

            // Check if the user exists
            if (user != null) {
                // Use BCrypt to check if the provided password matches the stored password
                if (bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
                    // Check the user's approval status
                    if (user.getStatus().equalsIgnoreCase("true")) {
                        // Generate a JWT token for the authenticated user
                        return new ResponseEntity<String>("{\"token\":\"" +
                                jwtUtil.generateToken(user.getEmail(), user.getRole()) + "\"}",
                                HttpStatus.OK);
                    } else {
                        // Return a message indicating that admin approval is required
                        return new ResponseEntity<String>("{\"message\":\"" + "wait for admin approval." + "\"}",
                                HttpStatus.BAD_REQUEST);
                    }
                }
            }

        } catch (Exception ex) {

            // Log any exceptions that occur during authentication
            log.error("An error occurred during login for email '{}': {}", requestMap.get("email"), ex.getMessage(), ex);
        }

        // Return a message indicating wrong credentials if authentication fails
        return new ResponseEntity<String>("{\"message\":\"" + "Wrong credentials." + "\"}",
                HttpStatus.BAD_REQUEST);
    }


    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    getMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User status updated successful", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User Id doesn't exists", HttpStatus.OK);
                }
            } else {
                CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // If something goes wrong, return a generic error response
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void getMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:-" + user + "\n is approved by \n ADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER:-" + user + "\n is disabled by \n ADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        }
    }

    // Check for admin or user and redirect to their page
    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            // extract email from JwtFilter
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (!userObj.equals(null)) {
                if (userObj.getPassword().equals(requestMap.get("oldPassword"))) {
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);

                    return CafeUtils.getResponseEntity("Password updated successful", HttpStatus.OK);
                }

                return CafeUtils.getResponseEntity("Wrong old password", HttpStatus.BAD_REQUEST);
            }

            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            // Retrieve user by email from the database
            User user = userDao.findByEmail(requestMap.get("email"));

            // Check if the user exists and if their email is not null or empty
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())){

                // Generate a new plain text password
                String newPassword = PasswordUtils.generateRandomPassword();

                // Hash the new password before saving it to the database (e.g., using BCrypt)
                String hashedPassword = bCryptPasswordEncoder.encode(newPassword);

                // Update the user's password in the database with the hashed version
                user.setPassword(hashedPassword);
                userDao.save(user);

                // Send an email with the new plain text password to the user
                emailUtils.forgotMail(user.getEmail(),"Credentials for Cafe Management System",newPassword);

                // Return success response indicating the user should check their email
                return CafeUtils.getResponseEntity("Check your email for new credentials",HttpStatus.OK);
            }

            // Return response if the email is not found in the database
            return CafeUtils.getResponseEntity("Email not found", HttpStatus.BAD_REQUEST);

        }catch (Exception ex){
            // Print the stack trace for debugging purposes
            ex.printStackTrace();
        }

        // Return a generic error response in case of an exception
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Category getCategoryFromMap(Map<String,String> requestMap, Boolean isAdd){
        // Category object
        Category category = new Category();

        if (isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }

        category.setName(requestMap.get("name"));

        return category;
    }

}
