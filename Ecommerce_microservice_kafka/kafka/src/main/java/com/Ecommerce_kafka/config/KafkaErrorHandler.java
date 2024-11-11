package com.Ecommerce_kafka.config;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.kafka.listener.ErrorHandler;

@Component
public class KafkaErrorHandler implements ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaErrorHandler.class);

    @Override
    public void handle(Exception thrownException, ConsumerRecord<?, ?> data) {
        if (thrownException instanceof ListenerExecutionFailedException) {
            logger.error("Error processing message {}: {}", data, thrownException.getMessage());
            // Implement retry or compensating actions here
        } else {
            logger.error("Unknown error processing message {}: {}", data, thrownException.getMessage());
        }
    }
}
