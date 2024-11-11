package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.*;
import com.Ecommerce_kafka.Exception.PropertyException;
import com.Ecommerce_kafka.Repository.BookingRepository;
import com.Ecommerce_kafka.Repository.PropertyRepository;
import jakarta.el.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    private final BookingRepository bookingRepository;

    public PropertyService(PropertyRepository propertyRepository, BookingRepository bookingRepository) {
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
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
            Property property = propertyRepository.findById(Long.valueOf(event.getPropertyId()))
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
        booking.setBookingDate((bookingRequest.getStartDate()).atStartOfDay());

        bookingRepository.save(booking);
    }
    public List<Property> getAvailableProperties() {
        return propertyRepository.findByStatus("AVAILABLE");
    }
    public Property updatePropertyStatus(Long propertyId, String status) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + propertyId));

        property.setStatus(status);
        return propertyRepository.save(property);
    }


}
