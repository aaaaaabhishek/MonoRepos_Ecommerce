package com.Ecommerce_kafka.Repository;

import com.Ecommerce_kafka.Entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
