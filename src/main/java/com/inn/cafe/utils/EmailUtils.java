package com.inn.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    JavaMailSender mailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("protechdevs86@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        if (list != null && list.size() > 0) {
            // Call the helper method to convert the List<String> to String[] and set CC
            message.setCc(getCcArray(list));
        }

        // Send message
        mailSender.send(message);
    }

    private String[] getCcArray(List<String> cclist) {
        // Create an array with the same size as the list
        String[] cc = new String[cclist.size()];

        // Convert the List to an array
        for (int i = 0; i < cclist.size(); i++) {
            cc[i] = cclist.get(i); // Populate the array with email addresses from the list
        }

        return cc; // Return the array
    }

}
