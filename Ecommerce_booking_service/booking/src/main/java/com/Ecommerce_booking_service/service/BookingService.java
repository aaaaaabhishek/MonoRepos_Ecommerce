package com.Ecommerce_booking_service.service;

import com.Ecommerce_booking_service.Entity.*;
import com.Ecommerce_booking_service.Exception.BookingNotFoundException;
import com.Ecommerce_booking_service.Repositary.BookingRepository;
import com.Ecommerce_booking_service.payload.BookingDto;
import com.Ecommerce_booking_service.topic.Topic;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final KafkaTemplate<String, BookingInitiatedEvent> kafkaTemplate;
    private final KafkaTemplate<String, BookingConfirmedEvent> bookingConfirmedKafkaTemplate;
    private final KafkaTemplate<String, BookingFailedEvent> bookingFailedEventKafkaTemplate;

    private final BookingRepository bookingRepository;
    private final ModelMapper mapper;

    @Autowired
    public BookingService(KafkaTemplate<String, BookingInitiatedEvent> kafkaTemplate,
                          KafkaTemplate<String, BookingConfirmedEvent> bookingConfirmedKafkaTemplate, KafkaTemplate<String, BookingFailedEvent> bookingFailedEventKafkaTemplate, BookingRepository bookingRepository, ModelMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.bookingConfirmedKafkaTemplate = bookingConfirmedKafkaTemplate;
        this.bookingFailedEventKafkaTemplate = bookingFailedEventKafkaTemplate;
        this.bookingRepository = bookingRepository;
        this.mapper=mapper;
    }


    // Fetch all bookings and map to BookingDto
    public List<BookingDto> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Fetch a booking by ID and map to BookingDto
    public Optional<BookingDto> getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::convertToDto);
    }

    // Save a booking (convert BookingDto to Booking)
    public BookingDto saveBooking(BookingDto bookingDto) {
        Booking booking = convertToEntity(bookingDto);
        Booking savedBooking = bookingRepository.save(booking);
        return convertToDto(savedBooking);
    }
    public BookingDto updateBooking(Long id, BookingDto bookingDetails) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setCustomerName(bookingDetails.getCustomerName());
            booking.setBookingDate(bookingDetails.getBookingDate());
            booking.setPropertyId(bookingDetails.getPropertyId());

            Booking updatedBooking = bookingRepository.save(booking);
            return convertToDto(updatedBooking);
        } else {
            throw new RuntimeException("Booking not found for id: " + id);
        }
    }


    // Delete a booking by ID
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // Convert Booking entity to BookingDto
    private BookingDto convertToDto(Booking booking) {
        return mapper.map(booking, BookingDto.class);
    }

    // Convert BookingDto to Booking entity
    private Booking convertToEntity(BookingDto bookingDto) {
        return mapper.map(bookingDto, Booking.class);
    }
    public void initiateBooking(BookingRequest request) {
        // Save booking with PENDING status
        Booking booking = new Booking();
        booking.setPropertyId(request.getPropertyId().toString());
        booking.setCustomerName(request.getCustomerName());
        booking.setStatus("PENDING");
        booking.setBookingDate(LocalDate.now()); // Assuming you want to set the current date as the booking date
        // Set the amount if available in the request (ensure BookingRequest has an amount field)
        booking.setAmount(request.getAmount()); // Assuming you add an amount field to Booking
        bookingRepository.save(booking);

        // Publish the BookingInitiatedEvent
        BookingInitiatedEvent event = new BookingInitiatedEvent(
                booking.getBooking_id(),
                booking.getPropertyId(),
                booking.getCustomerName(),
                booking.getBookingDate(),
                booking.getAmount()
        );
        kafkaTemplate.send(Topic.BOOKING_INITIATED.getTopicName(), event)
                .thenApply(result -> {
                    System.out.println("Message sent successfully: " + result.getProducerRecord().value());
                    return result;
                })
                .exceptionally(ex -> {
                    System.err.println("Message sending failed: " + ex.getMessage());
                    return null;
                });


    }
}
