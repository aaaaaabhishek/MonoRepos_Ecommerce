package com.Ecommerce_payment_module.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ui.Model;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingInitiatedEvent {
    private Long bookingId;          // The ID of the newly created booking
    private String propertyId;       // The ID of the property being booked
    private String customerName;     // The name of the customer making the booking
    private LocalDate bookingDate;   // The date when the booking was made
    private Double amount;           // The amount for the booking (e.g., total price)


}