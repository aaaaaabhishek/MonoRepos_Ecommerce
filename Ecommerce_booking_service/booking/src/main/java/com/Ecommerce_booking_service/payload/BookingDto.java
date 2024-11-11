package com.Ecommerce_booking_service.payload;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private String customerName;
    private LocalDate bookingDate;
    private String propertyId;
    private Long amount;
}
