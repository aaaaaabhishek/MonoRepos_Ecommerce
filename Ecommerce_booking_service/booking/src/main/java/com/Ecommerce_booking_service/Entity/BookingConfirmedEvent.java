package com.Ecommerce_booking_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmedEvent {

    private Long bookingId;
    private String propertyId;
    @Override
    public String toString() {
        return "BookingConfirmedEvent{" +
                "bookingId=" + bookingId +
                ", propertyId='" + propertyId + '\'' +
                '}';
    }

}