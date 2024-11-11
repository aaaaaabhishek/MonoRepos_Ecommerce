package com.Ecommerce_booking_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingFailedEvent {
    private Long bookingId;
    private String propertyId;
    private String reason="Payment processing failed"; // Reason for the failure

    // Constructors, Getters, and Setters

    public BookingFailedEvent(Long bookingId, String propertyId){
        this.bookingId = bookingId;
        this.propertyId = propertyId;
    }
}