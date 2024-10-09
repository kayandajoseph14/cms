package com.inn.cafe.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private String secret = "Admin@@@&&&###$$$";  // Secret key used to sign the JWT

    // Extracts the username (subject) from the JWT token
    public String extractUsername(String token) {
        return extractClamis(token, Claims::getSubject);
    }

    // Extracts the expiration date from the JWT token
    public Date extractExpiration(String token) {
        return extractClamis(token, Claims::getExpiration);
    }

    // Generic method to extract specific claims from the JWT
    public <T> T extractClamis(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Extract all claims from the token
        return claimsResolver.apply(claims);  // Apply the function to get a specific claim
    }

    // Parses the JWT and retrieves all claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)  // Use the secret key to parse the token
                .parseClaimsJws(token)
                .getBody();
    }

    // Checks if the JWT token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generates a new JWT token for a given username and role
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);  // Add role as a claim
        return createToken(claims, username);
    }

    // Creates the JWT token with the provided claims and username
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)  // Set the username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Set the issued date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))  // Set expiration to 10 hours
                .signWith(SignatureAlgorithm.HS256, secret)  // Sign the token using HS256 algorithm
                .compact();  // Create and return the JWT string
    }

    // Validates the JWT token by checking the username and expiration date
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
