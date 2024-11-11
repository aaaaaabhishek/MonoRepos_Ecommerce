package com.Ecommerce_booking_service.Repositary;

import com.Ecommerce_booking_service.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
