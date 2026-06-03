package com.hostel.management.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Verify Your Email - Hostel Management System");
            message.setText("Welcome to Hostel Management System!\n\n" +
                    "Please verify your email by clicking the link below:\n" +
                    "http://localhost:4200/verify-email?token=" + token + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you didn't register, please ignore this email.\n\n" +
                    "Best regards,\nHostel Management Team");

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}
