package com.Ecommerce_booking_service.service;

import com.Ecommerce_booking_service.Entity.*;
import com.Ecommerce_booking_service.Exception.BookingNotFoundException;
import com.Ecommerce_booking_service.Repositary.BookingRepository;
import com.Ecommerce_booking_service.topic.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingEventListenerService {

    private final BookingRepository bookingRepository;
    private final KafkaTemplate<String, BookingInitiatedEvent> kafkaTemplate;
    private final KafkaTemplate<String, BookingConfirmedEvent> bookingConfirmedKafkaTemplate;
    private final KafkaTemplate<String, BookingFailedEvent> bookingFailedEventKafkaTemplate;

    @Autowired
    public BookingEventListenerService(BookingRepository bookingRepository, KafkaTemplate<String, BookingInitiatedEvent> kafkaTemplate, KafkaTemplate<String, BookingConfirmedEvent> bookingConfirmedKafkaTemplate, KafkaTemplate<String, BookingFailedEvent> bookingFailedEventKafkaTemplate) {
        this.bookingRepository = bookingRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.bookingConfirmedKafkaTemplate = bookingConfirmedKafkaTemplate;
        this.bookingFailedEventKafkaTemplate = bookingFailedEventKafkaTemplate;
    }

    @KafkaListener(topics = "payment-success", groupId = "booking-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
        bookingConfirmedKafkaTemplate.send(Topic.BOOKING_CONFIRMED.getTopicName(), new BookingConfirmedEvent(booking.getBooking_id(), booking.getPropertyId()));
    }

    @KafkaListener(topics = "payment-failed", groupId = "booking-group")
    public void handlePaymentFailure(PaymentFailedEvent event) {
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        booking.setStatus("FAILED");
        bookingRepository.save(booking);
        bookingFailedEventKafkaTemplate.send(Topic.BOOKING_FAILED.getTopicName(), new BookingFailedEvent(booking.getBooking_id(), booking.getPropertyId()));
    }
}
