package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.*;
import com.Ecommerce_kafka.Exception.PaymentException;
import com.Ecommerce_kafka.Repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public final KafkaTemplate<String, PaymentFailedEvent> failedKafkaTemplate;
    public final KafkaTemplate<String,PaymentSuccessEvent> successKafkaTemplate;

    private final PaymentRepository paymentRepository;
    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(KafkaTemplate<String, PaymentFailedEvent> failedKafkaTemplate, KafkaTemplate<String, PaymentSuccessEvent> successKafkaTemplate, PaymentRepository paymentRepository) {
        this.failedKafkaTemplate = failedKafkaTemplate;
        this.successKafkaTemplate = successKafkaTemplate;
        this.paymentRepository = paymentRepository;
    }

    @CircuitBreaker(name= "paymentService", fallbackMethod = "paymentFallback")
    @Retry(name = "paymentService", fallbackMethod = "paymentFallback")

    public void processPayment(BookingRequest bookingRequest) {
        try {
            // Process the payment
            boolean paymentSuccess = processPaymentLogic(bookingRequest);

            if (paymentSuccess) {
                // Save payment info to DB
                Payment payment =Payment.builder().userId(bookingRequest.getUserId()).propertyId(bookingRequest.getPropertyId()).paymentStatus("SUCCESS").build();



                paymentRepository.save(payment);

                // Send payment success event
                successKafkaTemplate.send("payment-success", new PaymentSuccessEvent(bookingRequest.getPropertyId()));
            } else {
                throw new PaymentException("Payment failed for property " + bookingRequest.getPropertyId(), HttpStatus.EXPECTATION_FAILED);
            }

        } catch (Exception ex) {
            logger.error("Error during payment processing: {}", ex.getMessage());
            throw new PaymentException("Payment failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @KafkaListener(topics = "property-booking-requests", groupId = "payment-group")
    public void onBookingRequest(BookingRequest bookingRequest) {
        processPayment(bookingRequest);
    }

    public void paymentFallback(BookingRequest bookingRequest, Throwable throwable) {
        logger.warn("Payment fallback triggered for property: {} due to {}", bookingRequest.getPropertyId(), throwable.getMessage());

        // Send failure event to Kafka
        failedKafkaTemplate.send("booking-failed", new PaymentFailedEvent(bookingRequest.getPropertyId()));
    }

    private boolean processPaymentLogic(BookingRequest bookingRequest) {
        // Simulate payment processing logic
        return true;  // Payment success
    }
}
