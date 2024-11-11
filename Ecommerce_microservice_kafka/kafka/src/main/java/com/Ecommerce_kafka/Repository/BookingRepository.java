package com.Ecommerce_kafka.Repository;
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Custom query to find a booking by its propertyId, if necessary
    Optional<Booking> findByPropertyId(Long propertyId);

    // You can also add other custom methods if needed
}
