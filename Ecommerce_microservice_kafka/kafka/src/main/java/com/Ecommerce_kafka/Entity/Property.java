package com.Ecommerce_kafka.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Property {
    @Id
    private String propertyId;
    private String name;
    private String status; // e.g., AVAILABLE, BOOKED
    private double price;
}
