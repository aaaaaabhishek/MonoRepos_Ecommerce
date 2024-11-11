package com.Ecommerce_kafka.Entity;

import lombok.Data;

@Data
public class BookingEvent {
    private String bookingId;
    private String propertyId;
    private String userId;
    private String status; // PENDING, CONFIRMED
}