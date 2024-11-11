package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.BookingRequest;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
            bookingRepository.save(new Booking(bookingRequest.getUserId(), bookingRequest.getPropertyId(), "PENDING"));

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
            Booking booking = bookingRepository.findByPropertyId(paymentSuccessEvent.getPropertyId());
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);

            // Send event to notify other services
            kafkaTemplate.send("property-booking-confirmed", new PropertyBookingConfirmedEvent(paymentSuccessEvent.getPropertyId()));

        } catch (Exception ex) {
            logger.error("Error while confirming booking: {}", ex.getMessage());
            throw new BookingException("Booking confirmation failed", ex);
        }
    }
}
