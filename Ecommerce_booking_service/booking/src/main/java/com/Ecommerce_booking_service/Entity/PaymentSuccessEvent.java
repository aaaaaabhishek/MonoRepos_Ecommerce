package com.Ecommerce_booking_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessEvent {
    private Long bookingId; // The ID of the successfully paid booking
    private String transactionId; // Unique identifier for the payment transaction
    private double amount; // Amount paid
    private String currency; // Currency of the payment
    private String paymentMethod;
}
