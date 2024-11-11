package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.Booking;
import com.Ecommerce_kafka.Entity.BookingRequest;
import com.Ecommerce_kafka.Entity.Property;
import com.Ecommerce_kafka.Entity.PropertyBookingConfirmedEvent;
import com.Ecommerce_kafka.Repository.PropertyRepository;
import com.Ecommerce_kafka.exception.PropertyException;
import com.Ecommerce_kafka.exception.PropertyNotFoundException;
import com.Ecommerce_kafka.model.Property;
import com.Ecommerce_kafka.repository.PropertyRepository;
import jakarta.el.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Method for Kafka Listener - updates status to "BOOKED"
    @KafkaListener(topics = "property-booking-confirmed", groupId = "property-group")
    public void updatePropertyStatus(PropertyBookingConfirmedEvent event) {
        try {
            Property property = propertyRepository.findById(event.getPropertyId())
                    .orElseThrow(() -> new PropertyNotFoundException("Property not found: " + event.getPropertyId()));

            property.setStatus("BOOKED");
            propertyRepository.save(property);

        } catch (Exception ex) {
            logger.error("Error updating property status: {}", ex.getMessage());
            throw new PropertyException("Failed to update property status", ex);
        }
    }

    // Method for Kafka Listener - updates status to "AVAILABLE" if booking fails
    @KafkaListener(topics = "booking-failed", groupId = "property-group")
    public void compensateBooking(BookingFailedEvent event) {
        try {
            Property property = propertyRepository.findById(event.getPropertyId())
                    .orElseThrow(() -> new PropertyNotFoundException("Property not found: " + event.getPropertyId()));

            property.setStatus("AVAILABLE");
            propertyRepository.save(property);

        } catch (Exception ex) {
            logger.error("Error reverting property status: {}", ex.getMessage());
        }
    }

    // Method to add a property (for controller)
    public Property addProperty(Property property) {
        return propertyRepository.save(property);
    }

    // Method to find a property by ID (for controller)
    public Property findPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + propertyId));
    }
    public void createBooking(BookingRequest bookingRequest) {
        Booking booking = new Booking();
        booking.setPropertyId(bookingRequest.getPropertyId());
        booking.setUserId(bookingRequest.getUserId());
        booking.setBookingDate(bookingRequest.getBookingDate());

        bookingRepository.save(booking);
    }

}
