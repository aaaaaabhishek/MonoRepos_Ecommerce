package com.Ecommerce_kafka.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {

    private String paymentId;
    private String userId;
    private String propertyId;
    private String paymentStatus;
}