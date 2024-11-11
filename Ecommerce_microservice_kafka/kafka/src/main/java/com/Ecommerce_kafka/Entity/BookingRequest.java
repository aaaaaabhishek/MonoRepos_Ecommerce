package com.Ecommerce_kafka.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

    private Long propertyId;
    private Long userId;
    private LocalDate startDate;
    public LocalDate endDate;
    private String bookingStatus;

    public BookingRequest(Long propertyId) {
        this.propertyId=propertyId;
    }
}