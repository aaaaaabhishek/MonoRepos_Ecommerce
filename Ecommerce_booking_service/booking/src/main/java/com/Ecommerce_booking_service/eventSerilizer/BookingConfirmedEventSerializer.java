package com.Ecommerce_booking_service.eventSerilizer;

import com.Ecommerce_booking_service.Entity.BookingConfirmedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class BookingConfirmedEventSerializer implements Serializer<BookingConfirmedEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Configuration logic if needed
    }

    @Override
    public byte[] serialize(String topic, BookingConfirmedEvent data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing BookingConfirmedEvent", e);
        }
    }

    @Override
    public void close() {
        // Cleanup logic if needed
    }
}
