package com.inn.cafe.JWT;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // Utility class for handling JWT token operations

    @Autowired
    private CustomerUsersDetailsService service; // Custom service to load user details from the database

    Claims claims = null; // Holds the claims extracted from the JWT token
    private String userName = null; // Holds the extracted username from the token

    /**
     * The main method that processes each incoming HTTP request.
     * It checks if the request contains a valid JWT token and, if valid, sets the user authentication.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        // If the request is for login, signup, or forgot password, skip JWT validation
        if (httpServletRequest.getServletPath().matches("/user/login|/user/signup|/user/forgotPassword")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse); // Pass the request to the next filter
        } else {
            // Extract the Authorization header from the request
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;

            // Check if the Authorization header contains a Bearer token
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // Remove "Bearer " prefix to get the token
                userName = jwtUtil.extractUsername(token); // Extract the username from the token
                claims = jwtUtil.extractAllClaims(token); // Extract all claims from the token
            }

            // Check if the username is not null and no authentication is set in the security context
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from the database based on the extracted username
                UserDetails userDetails = service.loadUserByUsername(userName);

                // Validate the token and set the authentication if valid
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Create an authentication token

                    // Set additional details like remote IP, etc.
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );

                    // Set the authenticated user in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            // Pass the request and response to the next filter in the chain
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    /**
     * Method to check if the current user is an admin.
     * It looks at the 'role' claim in the JWT token.
     *
     * @return true if the role is "admin", false otherwise
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) claims.get("role")); // Check if the role claim equals "admin"
    }

    /**
     * Method to check if the current user is a regular user.
     * It looks at the 'role' claim in the JWT token.
     *
     * @return true if the role is "user", false otherwise
     */
    public boolean isUser() {
        return "user".equalsIgnoreCase((String) claims.get("role")); // Check if the role claim equals "user"
    }

    /**
     * Method to get the username of the currently authenticated user.
     *
     * @return The username extracted from the JWT token
     */
    public String getCurrentUser() {
        return userName; // Return the username extracted from the token
    }
}
