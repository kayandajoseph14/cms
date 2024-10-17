package com.inn.cafe.utils;

import java.security.SecureRandom;

public class PasswordUtils {

    // Set of characters that can be used in the password (A-Z, a-z, 0-9)
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Length of the generated password
    private static final int PASSWORD_LENGTH = 10;

    // Method to generate a random password of length PASSWORD_LENGTH
    public static String generateRandomPassword(){

        // SecureRandom provides cryptographically strong random numbers
        SecureRandom random = new SecureRandom();

        // StringBuilder used to construct the password character by character
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Loop to add random characters from CHARACTERS to the password
        for (int i = 0; i < PASSWORD_LENGTH; i++){
            // Pick a random character from CHARACTERS and append it to password
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        // Convert StringBuilder to a string and return the generated password
        return password.toString();
    }
}
