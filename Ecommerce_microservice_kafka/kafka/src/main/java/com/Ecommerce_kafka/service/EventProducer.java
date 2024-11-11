package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.BookingEvent;
import com.Ecommerce_kafka.Entity.PropertyEvent;
import com.Ecommerce_kafka.Entity.PropertyPriceUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPropertyEvent(PropertyEvent event) {
        kafkaTemplate.send("property-status-events", event);
    }

    public void sendBookingEvent(BookingEvent event) {
        kafkaTemplate.send("booking-events", event);
    }

    public void sendPriceUpdate(PropertyPriceUpdate update) {
        kafkaTemplate.send("property-price-updates", update);
    }
}
