package com.Ecommerce_payment_module.Service;

import com.Ecommerce_payment_module.Exception.PaymentProcessingException;
import com.Ecommerce_payment_module.entity.BookingInitiatedEvent;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentEventListenerService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListenerService.class);

    private static double latestAmount;
    private static String latestOrderId;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Kafka listener to handle booking initiated events
    @KafkaListener(topics = "booking-initiated-topic", groupId = "payment-service-group")
    public void handleBookingInitiatedEvent(BookingInitiatedEvent event) {
        try {
            if (event.getAmount() <= 0) {
                throw new PaymentProcessingException("Invalid payment amount");
            }
            String orderId = createOrder(event.getAmount());
            latestAmount = event.getAmount();
            latestOrderId = orderId;
            notifyFrontend((Model) event);

        } catch (PaymentProcessingException e) {
            logger.error("Payment processing error: {}", e.getMessage());
            // Handle failure, e.g., send a failure message to the frontend
        } catch (Exception e) {
            logger.error("Failed to create order: {}", e.getMessage(), e);
            // Handle failure, e.g., send a failure message to the frontend
        }
    }

    private String createOrder(double amount) throws Exception {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Convert amount to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt#2");

        Order orderResponse = razorpay.orders.create(orderRequest);
        return orderResponse.get("id").toString();
    }

    // Method to prepare the model for the payment page
    public String showPaymentPage(Model model) {
        Double amount = getLatestAmount(); // Get the latest amount
        String orderId = getLatestOrderId(); // Get the latest order ID

        model.addAttribute("amount", amount);
        model.addAttribute("orderId", orderId);
        return "Razorpay Checkout"; // Ensure the HTML file is named "Razorpay Checkout.html"
    }

    // Getters for the latest payment details
    public static double getLatestAmount() {
        return latestAmount;
    }

    public static String getLatestOrderId() {
        return latestOrderId;
    }
    private String notifyFrontend(Model model) {
        model.addAttribute("amount", latestAmount);
        model.addAttribute("orderId", latestOrderId);
        // Redirect to the payment page or render the view accordingly
          return "Razorpay Checkout"; // Ensure the HTML file is named "Razorpay Checkout.html"
    }

    // Send success event to Kafka after payment
    public void onPaymentSuccess(String orderId) {
        String successMessage = String.format("Payment successful for Order ID: %s", orderId);
        kafkaTemplate.send("payment-success-topic", orderId);
        logger.info(successMessage);
    }
    public void onPaymentfailure(String orderId) {
        String successMessage = String.format("Payment failure for Order ID: %s", orderId);
        kafkaTemplate.send("payment-success-topic", orderId);
        logger.info(successMessage);
    }
}
