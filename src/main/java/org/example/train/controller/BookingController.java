package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.dto.request.BookingRequest;
import org.example.train.dto.response.BookingResponse;
import org.example.train.mappers.BookingMapper;
import org.example.train.model.Booking;
import org.example.train.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingResponse> book(@Valid @RequestBody BookingRequest request) {

        Booking booking = bookingService.book(
                request.personId(),
                request.scheduleId(),
                request.fromStationId(),
                request.toStationId(),
                request.numSeats(),
                request.pricePerSeat()
        );

        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> myBookings(
            @RequestHeader("X-Person-Id") UUID personId) {
        List<BookingResponse> bookings = bookingService.findByPerson(personId).stream()
                .map(bookingMapper::toResponse)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingMapper.toResponse(bookingService.findById(id)));
    }

    // admin only
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<BookingResponse>> findBySchedule(@PathVariable UUID scheduleId) {
        List<BookingResponse> bookings = bookingService.findBySchedule(scheduleId).stream()
                .map(bookingMapper::toResponse)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingMapper.toResponse(bookingService.cancel(id)));
    }

}