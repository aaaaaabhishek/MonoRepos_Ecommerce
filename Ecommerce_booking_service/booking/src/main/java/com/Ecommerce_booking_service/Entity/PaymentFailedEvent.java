package com.Ecommerce_booking_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {
    private Long bookingId;  // The ID of the failed booking
    private String propertyId;
    private String reason;    // Reason for the failure (optional)
}