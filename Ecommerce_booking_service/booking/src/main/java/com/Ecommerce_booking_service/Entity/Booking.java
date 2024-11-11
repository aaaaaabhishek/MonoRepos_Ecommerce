package com.Ecommerce_booking_service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long booking_id;
    private String customerName;
    private LocalDate bookingDate;
    private String propertyId;
    private String status; // e.g., PENDING, CONFIRMED, FAILED
    private String transactionId; // Transaction ID from the payment
    private double amount; // Amount paid
    private String currency; // Currency of the payment
    private String paymentMethod; // Method used for payment
}