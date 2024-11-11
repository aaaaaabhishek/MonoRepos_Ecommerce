package com.Ecommerce_kafka.Entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class Payment {

    private Long userId;
    private Long propertyId;
    private String paymentStatus;
}
