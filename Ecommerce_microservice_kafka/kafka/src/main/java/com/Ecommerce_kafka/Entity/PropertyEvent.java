package com.Ecommerce_kafka.Entity;

import lombok.Data;

@Data
public class PropertyEvent {
    private String propertyId;
    private String status; // AVAILABLE, BOOKED
}