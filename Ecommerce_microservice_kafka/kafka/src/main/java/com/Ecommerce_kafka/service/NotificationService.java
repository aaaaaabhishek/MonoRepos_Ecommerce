package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.PropertyBookingConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "property-booking-confirmed", groupId = "notification-group")
    public void sendBookingConfirmation(PropertyBookingConfirmedEvent event) {
        try {
            // Send booking confirmation email
            emailService.sendConfirmationEmail(event.getPropertyId());
            logger.info("Booking confirmation email sent for property: {}", event.getPropertyId());

        } catch (Exception ex) {
            logger.error("Error sending booking confirmation: {}", ex.getMessage());
        }
    }

    @KafkaListener(topics = "booking-failed", groupId = "notification-group")
    public void sendFailureNotification(BookingFailedEvent event) {
        try {
            // Send booking failure email
            emailService.sendFailureEmail(event.getPropertyId());
            logger.info("Booking failure email sent for property: {}", event.getPropertyId());

        } catch (Exception ex) {
            logger.error("Error sending booking failure notification: {}", ex.getMessage());
        }
    }
}
