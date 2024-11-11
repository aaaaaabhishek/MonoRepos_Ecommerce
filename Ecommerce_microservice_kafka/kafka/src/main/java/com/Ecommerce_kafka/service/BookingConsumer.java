package com.Ecommerce_kafka.service;

import com.Ecommerce_kafka.Entity.BookingRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
public class BookingConsumer {

    private final BlockingQueue<BookingRequest> queue = new ArrayBlockingQueue<>(100);

    @KafkaListener(topics = "property-booking-requests", groupId = "booking-group")
    public void consumeBookingRequest(BookingRequest bookingRequest) throws InterruptedException {
        queue.put(bookingRequest); // Backpressure control
        processQueue();
    }

    private void processQueue() {
        while (!queue.isEmpty()) {
            BookingRequest request = queue.poll();
            // Process the request
        }
    }
}
