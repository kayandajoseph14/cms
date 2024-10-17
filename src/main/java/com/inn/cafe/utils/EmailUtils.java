package com.inn.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    JavaMailSender mailSender; // Inject JavaMailSender to handle email sending

    // Method to send a simple text email with optional CC list
    public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
        SimpleMailMessage message = new SimpleMailMessage(); // Create a simple email message
        message.setFrom("protechdevs86@gmail.com"); // Set sender's email address
        message.setTo(to); // Set recipient's email address
        message.setSubject(subject); // Set the subject of the email
        message.setText(text); // Set the email body text

        if (list != null && list.size() > 0) {
            // If there are CC recipients, set them in the message
            message.setCc(getCcArray(list));
        }

        // Send the constructed email
        mailSender.send(message);
    }

    // Helper method to convert a List<String> to a String[] array for CC
    private String[] getCcArray(List<String> cclist) {
        String[] cc = new String[cclist.size()]; // Create an array for CC addresses

        // Loop through the list and populate the array
        for (int i = 0; i < cclist.size(); i++) {
            cc[i] = cclist.get(i);
        }

        return cc; // Return the array of CC addresses
    }

    // Method to send a password recovery email with HTML content
    public void forgotMail(String to, String subject, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage(); // Create a MIME message for HTML content
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // Helper to handle complex email formatting
        helper.setFrom("protechdevs86@gmail.com"); // Set sender's email address
        helper.setTo(to); // Set recipient's email address
        helper.setSubject(subject); // Set the subject of the email

        // HTML message with login details and a link to the system
        String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> "
                + to + " <br><b>Password: </b> " + password
                + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";

        message.setContent(htmlMsg, "text/html"); // Set the email content to HTML format

        // Send the email
        mailSender.send(message);
    }
}
