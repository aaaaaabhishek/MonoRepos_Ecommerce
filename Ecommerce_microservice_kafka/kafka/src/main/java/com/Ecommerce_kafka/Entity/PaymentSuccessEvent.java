package com.Ecommerce_kafka.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessEvent {
    private Long bookingId; // The ID of the successfully paid booking
    private Long  propertyId;
    private String transactionId; // Unique identifier for the payment transaction
    private double amount; // Amount paid
    private String currency; // Currency of the payment
    private String paymentMethod;

    public PaymentSuccessEvent(Long propertyId) {
        this.propertyId=propertyId;
    }
}
