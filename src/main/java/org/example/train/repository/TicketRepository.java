package org.example.train.repository;

import org.example.train.model.Booking;
import org.example.train.model.Schedule;
import org.example.train.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByBooking(Booking booking);

    List<Ticket> findBySchedule(Schedule schedule);

    // used to find which seat numbers are already taken on a schedule
    List<Ticket> findByScheduleAndBookingStatus(
            Schedule schedule,
            org.example.train.model.enums.BookingStatus status
    );

    boolean existsByScheduleAndSeatNumber(Schedule schedule, int seatNumber);
}