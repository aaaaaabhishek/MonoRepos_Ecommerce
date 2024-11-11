package com.Ecommerce_kafka.Repository;

import com.Ecommerce_kafka.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Custom query to find a booking by its propertyId, if necessary
    Optional<Booking> findByPropertyId(Long propertyId);

    // You can also add other custom methods if needed
}
