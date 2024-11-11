package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.BookingEvent;
import com.Ecommerce_kafka.Entity.PropertyEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {

    @KafkaListener(topics = "booking-events", groupId = "booking_group")
    public void listenBookingEvent(BookingEvent event) {
        // Handle booking event
        System.out.println("Received booking event: " + event);
    }

    @KafkaListener(topics = "property-status-events", groupId = "property_group")
    public void listenPropertyEvent(PropertyEvent event) {
        // Handle property event
        System.out.println("Received property event: " + event);
    }
}
