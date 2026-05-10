package org.example.train.repository;

import org.example.train.model.Route;
import org.example.train.model.Schedule;
import org.example.train.model.Train;
import org.example.train.model.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    List<Schedule> findByTrain(Train train);

    List<Schedule> findByRoute(Route route);

    List<Schedule> findByStatus(ScheduleStatus status);

    // all schedules for a route departing after a given time — used for journey search
    List<Schedule> findByRouteAndDepartureTimeAfter(Route route, LocalDateTime after);

    // all schedules for a specific train on a specific day — used for admin delay reporting
    List<Schedule> findByTrainAndDepartureTimeBetween(
            Train train,
            LocalDateTime from,
            LocalDateTime to
    );

    // count confirmed tickets on a schedule — used for overbooking check
    @Query("SELECT COUNT(t) FROM Ticket t " +
            "WHERE t.schedule = :schedule " +
            "AND t.booking.status = org.example.train.model.enums.BookingStatus.CONFIRMED")
    int countConfirmedTicketsBySchedule(@Param("schedule") Schedule schedule);
}