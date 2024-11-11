package com.Ecommerce_kafka.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyBookingConfirmedEvent {
    private Long propertyId;
    private Long bookingId;
    private Long userId;
    private String bookingDate;

}
