package com.Ecommerce_kafka.Entity;

import lombok.Data;

@Data
public class PropertyPriceUpdate {
    private String propertyId;
    private double price;
}
