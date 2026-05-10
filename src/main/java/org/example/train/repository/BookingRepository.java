package org.example.train.repository;

import org.example.train.model.Booking;
import org.example.train.model.Person;
import org.example.train.model.Schedule;
import org.example.train.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByPerson(Person person);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByPersonAndStatus(Person person, BookingStatus status);

    // all bookings that include a ticket on a given schedule — used for admin view and delay notification
    @Query("SELECT DISTINCT b FROM Booking b JOIN b.tickets t WHERE t.schedule = :schedule")
    List<Booking> findBySchedule(@Param("schedule") Schedule schedule);
}