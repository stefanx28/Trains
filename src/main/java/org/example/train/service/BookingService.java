package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.model.Booking;
import org.example.train.model.Person;
import org.example.train.model.Schedule;
import org.example.train.model.Station;
import org.example.train.model.Ticket;
import org.example.train.model.enums.BookingStatus;
import org.example.train.repository.BookingRepository;
import org.example.train.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PersonService personService;
    private final ScheduleService scheduleService;
    private final StationService stationService;
    private final EmailService emailService;

    @Transactional
    public Booking book(UUID personId, UUID scheduleId,
                        UUID fromStationId, UUID toStationId,
                        int numSeats, BigDecimal pricePerSeat) {

        Person person = personService.findById(personId);
        Schedule schedule = scheduleService.findById(scheduleId);
        Station from = stationService.findById(fromStationId);
        Station to = stationService.findById(toStationId);

        // check overbook
        int available = scheduleService.getAvailableSeats(scheduleId);
        if (numSeats > available) {
            throw new IllegalStateException("Not enough seats available.");
        }

        Booking booking = new Booking();
        booking.setPerson(person);
        booking.setBookedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < numSeats; i++) {
            int seat = findNextAvailableSeat(schedule);

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setSchedule(schedule);
            ticket.setFromStation(from);
            ticket.setToStation(to);
            ticket.setSeatNumber(seat);
            ticket.setPrice(pricePerSeat);


            ticketRepository.save(ticket);
            tickets.add(ticket);
        }

        booking.setTickets(tickets);

        emailService.sendConfirmation(person, booking, tickets);
        return booking;
    }

    @Transactional
    public Booking cancel(UUID bookingId) {
        Booking booking = findById(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking findById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }

    public List<Booking> findByPerson(UUID personId) {
        Person person = personService.findById(personId);
        return bookingRepository.findByPerson(person);
    }

    public List<Booking> findBySchedule(UUID scheduleId) {
        Schedule schedule = scheduleService.findById(scheduleId);
        return bookingRepository.findBySchedule(schedule);
    }

    // finds the lowest available seat number on a schedule
    private int findNextAvailableSeat(Schedule schedule) {
        List<Ticket> taken = ticketRepository.findByScheduleAndBookingStatus(
                schedule, BookingStatus.CONFIRMED);
        List<Integer> takenSeats = taken.stream()
                .map(Ticket::getSeatNumber)
                .toList();

        for (int seat = 1; seat <= schedule.getTrain().getTotalSeats(); seat++) {
            if (!takenSeats.contains(seat)) return seat;
        }
        throw new IllegalStateException("No seats available on schedule: " + schedule.getId());
    }
}