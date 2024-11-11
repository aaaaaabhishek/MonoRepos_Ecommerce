package com.Ecommerce_kafka.config;

import com.Ecommerce_kafka.Entity.BookingEvent;
import com.Ecommerce_kafka.Entity.PropertyEvent;
import com.Ecommerce_kafka.Entity.PropertyPriceUpdate;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaStreamsConfig {

    @Bean
    public KStream<String, BookingEvent> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, BookingEvent> stream = streamsBuilder.stream("property-booking-events");

        // Filter confirmed bookings
        KStream<String, BookingEvent> confirmedBookings = stream.filter(
                (key, value) -> value.getStatus().equals("CONFIRMED")
        );

        // Perform aggregation to count confirmed bookings per property
        confirmedBookings.groupByKey()
                .count(Materialized.as("confirmed-booking-counts"))
                .toStream()
                .to("property-confirmed-bookings");

        return stream;
    }
    @Bean
    public KStream<String, PropertyEvent> propertyStatusStream1(StreamsBuilder streamsBuilder) {
        KStream<String, PropertyEvent> stream = streamsBuilder.stream("property-status-events");

        // Filter for available properties
        KStream<String, PropertyEvent> availableProperties = stream.filter(
                (key, event) -> event.getStatus().equals("AVAILABLE")
        );

        // Send the available properties to another topic or process further
        availableProperties.to("available-properties");

        return stream;
    }
    @Bean
    public KTable<String, Long> bookingStatistics1(StreamsBuilder streamsBuilder) {
        KStream<String, BookingEvent> stream = streamsBuilder.stream("booking-events");

        // Count the number of bookings per property
        KTable<String, Long> bookingCounts = stream.groupByKey()
                .count(Materialized.as("booking-counts-store"));

        return bookingCounts;
    }
    @Bean
    public KTable<String, Double> averagePropertyPrice1(StreamsBuilder streamsBuilder) {
        KStream<String, PropertyPriceUpdate> stream = streamsBuilder.stream("property-price-updates");

        // Calculate the average price for each property
        KTable<String, Double> avgPrice = stream.groupByKey()
                .aggregate(
                        () -> 0.0, // Initial value
                        (key, value, aggregate) -> (aggregate + value.getPrice()) / 2,
                        Materialized.with(Serdes.String(), Serdes.Double())
                );

        return avgPrice;
    }

    @Bean
    public KStream<String, PropertyEvent> propertyStatusStream(StreamsBuilder streamsBuilder) {
        KStream<String, PropertyEvent> stream = streamsBuilder.stream("property-status-events");

        // Filter for available properties
        KStream<String, PropertyEvent> availableProperties = stream.filter(
                (key, event) -> event.getStatus().equals("AVAILABLE")
        );

        // Send the available properties to another topic or process further
        availableProperties.to("available-properties");

        return stream;
    }

    @Bean
    public KTable<String, Long> bookingStatistics(StreamsBuilder streamsBuilder) {
        KStream<String, BookingEvent> stream = streamsBuilder.stream("booking-events");

        // Count the number of bookings per property
        KTable<String, Long> bookingCounts = stream.groupByKey()
                .count(Materialized.as("booking-counts-store"));

        return bookingCounts;
    }

    @Bean
    public KTable<String, Double> averagePropertyPrice(StreamsBuilder streamsBuilder) {
        KStream<String, PropertyPriceUpdate> stream = streamsBuilder.stream("property-price-updates");

        // Calculate the average price for each property
        KTable<String, Double> avgPrice = stream.groupByKey()
                .aggregate(
                        () -> 0.0, // Initial value
                        (key, value, aggregate) -> (aggregate + value.getPrice()) / 2,
                        Materialized.with(Serdes.String(), Serdes.Double())
                );

        return avgPrice;
    }
}
