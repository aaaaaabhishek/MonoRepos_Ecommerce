package com.Ecommerce_kafka.service;

import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final PaymentRepository paymentRepository;
    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(KafkaTemplate<String, PaymentEvent> kafkaTemplate, PaymentRepository paymentRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentRepository = paymentRepository;
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    @Retry(name = "paymentService", maxAttempts = 3)
    public void processPayment(BookingRequest bookingRequest) {
        try {
            // Process the payment
            boolean paymentSuccess = processPaymentLogic(bookingRequest);

            if (paymentSuccess) {
                // Save payment info to DB
                paymentRepository.save(new Payment(bookingRequest.getUserId(), bookingRequest.getPropertyId(), "SUCCESS"));

                // Send payment success event
                kafkaTemplate.send("payment-success", new PaymentSuccessEvent(bookingRequest.getPropertyId()));
            } else {
                throw new PaymentException("Payment failed for property " + bookingRequest.getPropertyId());
            }

        } catch (Exception ex) {
            logger.error("Error during payment processing: {}", ex.getMessage());
            throw new PaymentException("Payment failed", ex);
        }
    }

    @KafkaListener(topics = "property-booking-requests", groupId = "payment-group")
    public void onBookingRequest(BookingRequest bookingRequest) {
        processPayment(bookingRequest);
    }

    public void paymentFallback(BookingRequest bookingRequest, Throwable throwable) {
        logger.warn("Payment fallback triggered for property: {} due to {}", bookingRequest.getPropertyId(), throwable.getMessage());

        // Send failure event to Kafka
        kafkaTemplate.send("booking-failed", new BookingFailedEvent(bookingRequest.getPropertyId()));
    }

    private boolean processPaymentLogic(BookingRequest bookingRequest) {
        // Simulate payment processing logic
        return true;  // Payment success
    }
}
