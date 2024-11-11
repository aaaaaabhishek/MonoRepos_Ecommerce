package com.Ecommerce_booking_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private Long propertyId;          // The ID of the property being booked
    private String customerName;      // The name of the customer making the booking
    private LocalDate bookingDate;    // The date for the booking
    private Double amount;           // The amount for the booking (e.g., total price)
}