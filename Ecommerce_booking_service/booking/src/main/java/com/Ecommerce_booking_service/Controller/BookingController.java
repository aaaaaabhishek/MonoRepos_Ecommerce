package com.Ecommerce_booking_service.Controller;

import com.Ecommerce_booking_service.Entity.BookingRequest;
import com.Ecommerce_booking_service.payload.BookingDto;
import com.Ecommerce_booking_service.service.BookingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    private static final Logger logger = Logger.getLogger(BookingController.class.getName());
    // Get all bookings
    @GetMapping
    @CircuitBreaker(name="bookingbreaker" ,fallbackMethod="bookingFallback")
    @Retry(name="bookingRetry" ,fallbackMethod = "bookingFallback")
    @RateLimiter(name = "userRateLimiter",fallbackMethod = "bookingFallback")
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Get booking by ID
    @GetMapping("/{id}")
    @CircuitBreaker(name="bookingbreaker" ,fallbackMethod="bookingFallback")
    @Retry(name="bookingRetry" ,fallbackMethod = "bookingFallback")
    @RateLimiter(name = "userRateLimiter",fallbackMethod = "bookingFallback")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        Optional<BookingDto> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new booking
    @PostMapping
    @CircuitBreaker(name="bookingbreaker" ,fallbackMethod="bookingFallback")
    @Retry(name="bookingRetry" ,fallbackMethod = "bookingFallback")
    @RateLimiter(name = "userRateLimiter",fallbackMethod = "bookingFallback")

    public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
        return bookingService.saveBooking(bookingDto);
    }

    // Update an existing booking
    @PutMapping("/{id}")
    @CircuitBreaker(name="bookingBreaker", fallbackMethod = "bookingFallback")
    @Retry(name="bookingRetry",fallbackMethod = "bookingFallback")
    @RateLimiter(name = "userRateLimiter",fallbackMethod = "bookingFallback")

    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long id, @RequestBody BookingDto bookingDetails) {
        try {
            BookingDto updatedBooking = bookingService.updateBooking(id, bookingDetails);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a booking by ID
    @DeleteMapping("/{id}")
    @CircuitBreaker(name="bookingbreaker" ,fallbackMethod="bookingFallback")
    @Retry(name="bookingRetry" ,fallbackMethod = "bookingFallback")
    @RateLimiter(name = "userRateLimiter",fallbackMethod = "bookingFallback")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/initiate")
    @CircuitBreaker(name="bookingbreaker" ,fallbackMethod="bookingFallback")
    @Retry(name="bookingRetry" ,fallbackMethod = "bookingFallback")
    @RateLimiter(name = "userRateLimiter",fallbackMethod = "bookingFallback")

    public ResponseEntity<BookingDto> initiateBooking(@RequestBody BookingRequest bookingRequest) {
        // Call the initiateBooking method from BookingService
        bookingService.initiateBooking(bookingRequest);

        return ResponseEntity.ok().build(); // Return an appropriate response
    }
    public ResponseEntity<BookingDto> bookingFallback(String bookid,Exception ex){//whatever give fallback method all varibale are same
        System.out.println("Fallback is exicuted because service is down :"+ex.getMessage());
        logger.severe("Fallback is exicuted because service is down :"+ex.getMessage());
        ex.printStackTrace();
        BookingDto fallbackBookingDto = BookingDto.builder()
                .id(null) // Or use a specific ID if needed
                .bookingDate(LocalDate.now()) // Default to the current date or a specific fallback date
                .customerName("Service Down") // Indicate that the service is unavailable
                .amount(0L) // Or another default value
                .propertyId("N/A") // Indicate no property ID is available
                .build();

        return new ResponseEntity<>(fallbackBookingDto, HttpStatus.BAD_REQUEST);
    }

}
