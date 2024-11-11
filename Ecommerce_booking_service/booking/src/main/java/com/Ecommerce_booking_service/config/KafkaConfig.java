package com.Ecommerce_booking_service.config;

import com.Ecommerce_booking_service.Entity.BookingConfirmedEvent;
import com.Ecommerce_booking_service.Entity.BookingInitiatedEvent;
import com.Ecommerce_booking_service.Entity.BookingFailedEvent;
import com.Ecommerce_booking_service.eventSerilizer.BookingConfirmedEventSerializer;
import com.Ecommerce_booking_service.eventSerilizer.BookingFailedEventSerializer;
import com.Ecommerce_booking_service.eventSerilizer.BookingInitiatedEventSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // Producer Factory for BookingInitiatedEvent
    @Bean
    public ProducerFactory<String, BookingInitiatedEvent> bookingInitiatedEventProducerFactory() {
        return createProducerFactory(BookingInitiatedEventSerializer.class);
    }

    @Bean
    public KafkaTemplate<String, BookingInitiatedEvent> bookingInitiatedKafkaTemplate() {
        return new KafkaTemplate<>(bookingInitiatedEventProducerFactory());
    }

    // Producer Factory for BookingConfirmedEvent
    @Bean
    public ProducerFactory<String, BookingConfirmedEvent> bookingConfirmedEventProducerFactory() {
        return createProducerFactory(BookingConfirmedEventSerializer.class);
    }

    @Bean
    public KafkaTemplate<String, BookingConfirmedEvent> bookingConfirmedKafkaTemplate() {
        return new KafkaTemplate<>(bookingConfirmedEventProducerFactory());
    }

    // Producer Factory for BookingFailedEvent
    @Bean
    public ProducerFactory<String, BookingFailedEvent> bookingFailedEventProducerFactory() {
        return createProducerFactory(BookingFailedEventSerializer.class);
    }

    @Bean
    public KafkaTemplate<String, BookingFailedEvent> bookingFailedEventKafkaTemplate() {
        return new KafkaTemplate<>(bookingFailedEventProducerFactory());
    }

    // Generic method to create a ProducerFactory
    private <T> ProducerFactory<String, T> createProducerFactory(Class<? extends org.apache.kafka.common.serialization.Serializer<T>> valueSerializer) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "your_kafka_broker_address"); // Replace with your broker address
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        config.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas to acknowledge
        config.put(ProducerConfig.RETRIES_CONFIG, 3); // Retry on failure
        config.put(ProducerConfig.LINGER_MS_CONFIG, 5); // Delay before sending to optimize batching
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // Batch size in bytes

        return new DefaultKafkaProducerFactory<>(config);
    }
}
