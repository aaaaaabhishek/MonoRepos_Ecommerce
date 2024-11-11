package com.Ecommerce_kafka.Repository;

import com.Ecommerce_kafka.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
