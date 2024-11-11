package com.Ecommerce_booking_service.eventSerilizer;

import com.Ecommerce_booking_service.Entity.BookingFailedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class BookingFailedEventSerializer implements Serializer<BookingFailedEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Configuration logic if needed
    }

    @Override
    public byte[] serialize(String topic, BookingFailedEvent data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing BookingFailedEvent", e);
        }
    }

    @Override
    public void close() {
        // Cleanup logic if needed
    }
}
