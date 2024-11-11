package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.Booking;
import com.Ecommerce_kafka.Entity.BookingRequest;
import com.Ecommerce_kafka.Entity.PaymentSuccessEvent;
import com.Ecommerce_kafka.Entity.PropertyBookingConfirmedEvent;
import com.Ecommerce_kafka.Exception.BookingException;
import com.Ecommerce_kafka.Repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService {

    private final KafkaTemplate<String, BookingRequest> kafkaTemplate;
    private final BookingRepository bookingRepository;
    private final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public BookingService(KafkaTemplate<String, BookingRequest> kafkaTemplate, BookingRepository bookingRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public void initiateBooking(BookingRequest bookingRequest) {
        try {
            // Save initial booking status to DB
            Booking booking = Booking.builder()
                            .userId(bookingRequest.getUserId())
                                    .propertyId(bookingRequest.getPropertyId())
                                            .bookingDate(bookingRequest.getStartDate().atStartOfDay())
                                                    .status(bookingRequest.getBookingStatus()).build();
            bookingRepository.save(booking);
            // Send event to Kafka to initiate the booking process
            kafkaTemplate.send("property-booking-requests", bookingRequest);

        } catch (Exception ex) {
            logger.error("Error while initiating booking: {}", ex.getMessage());
            throw new BookingException("Booking initiation failed", ex);
        }
    }

    @KafkaListener(topics = "payment-success", groupId = "booking-group")
    public void confirmBooking(PaymentSuccessEvent paymentSuccessEvent) {
        try {
            // Update booking status to confirmed
            Booking booking = bookingRepository.findByPropertyId(paymentSuccessEvent.getBookingId()).get();
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);

            // Send event to notify other services
            kafkaTemplate.send("property-booking-confirmed", new BookingRequest(paymentSuccessEvent.getPropertyId()));

        } catch (Exception ex) {
            logger.error("Error while confirming booking: {}", ex.getMessage());
            throw new BookingException("Booking confirmation failed", ex);
        }
    }
}
