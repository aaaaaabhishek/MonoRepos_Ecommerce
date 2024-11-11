package com.Ecommerce_kafka.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(String propertyId) {
        String subject = "Booking Confirmation";
        String body = "Your booking for property " + propertyId + " has been confirmed.";

        try {
            sendEmail("customer@example.com", subject, body);  // replace with actual customer email
        } catch (MailException e) {
            throw new RuntimeException("Error sending booking confirmation email", e);
        }
    }

    public void sendFailureEmail(String propertyId) {
        String subject = "Booking Failed";
        String body = "Unfortunately, your booking for property " + propertyId + " has failed.";

        try {
            sendEmail("customer@example.com", subject, body);  // replace with actual customer email
        } catch (MailException e) {
            throw new RuntimeException("Error sending booking failure email", e);
        }
    }

    private void sendEmail(String to, String subject, String body) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
