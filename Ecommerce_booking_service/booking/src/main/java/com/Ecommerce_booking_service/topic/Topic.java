package com.Ecommerce_booking_service.topic;
public enum Topic {
    BOOKING_INITIATED("booking-initiated"),
    BOOKING_CONFIRMED("booking-confirmed"),
    BOOKING_FAILED("booking-failed");
    private final String topicName;

    Topic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
