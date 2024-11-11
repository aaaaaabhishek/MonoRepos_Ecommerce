package com.Ecommerce_kafka.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {
    private Long bookingId;  // The ID of the failed booking
    private Long propertyId;
    private String reason;    // Reason for the failure (optional)
    public PaymentFailedEvent(Long propertyId) {
        this.propertyId=propertyId;
    }
}