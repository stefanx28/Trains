package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.model.Booking;
import org.example.train.model.Schedule;
import org.example.train.model.Train;
import org.example.train.model.Route;
import org.example.train.model.enums.ScheduleStatus;
import org.example.train.repository.BookingRepository;
import org.example.train.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TrainService trainService;
    private final RouteService routeService;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public Schedule add(UUID trainId, UUID routeId,
                        LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Schedule schedule = buildSchedule(trainId, routeId, departureTime, arrivalTime);
        return scheduleRepository.save(schedule);
    }

    public Schedule findById(UUID id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));
    }

    public List<Schedule> findByTrain(UUID trainId) {
        Train train = trainService.findById(trainId);
        return scheduleRepository.findByTrain(train);
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Schedule update(UUID id, UUID trainId, UUID routeId,
                           LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (!scheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Schedule not found: " + id);
        }

        Schedule updatedSchedule = buildSchedule(trainId, routeId, departureTime, arrivalTime);
        updatedSchedule.setId(id);

        return scheduleRepository.save(updatedSchedule);
    }

    public void delete(UUID id) {
        scheduleRepository.deleteById(id);
    }


    public Schedule setDelay(UUID scheduleId, int delayMinutes) {
        Schedule schedule = findById(scheduleId);
        int delay = schedule.getDelayMinutes();
        schedule.setDelayMinutes(delayMinutes + delay);
        schedule.setStatus(delayMinutes > 0 ? ScheduleStatus.DELAYED : ScheduleStatus.ON_TIME);
        scheduleRepository.save(schedule);

        // notify all passengers on this schedule
        List<Booking> affectedBookings = bookingRepository.findBySchedule(schedule);
        affectedBookings.forEach(booking ->
                emailService.sendDelayNotification(booking, delayMinutes));

        return schedule;
    }

    public int getAvailableSeats(UUID scheduleId) {
        Schedule schedule = findById(scheduleId);
        int confirmed = scheduleRepository.countConfirmedTicketsBySchedule(schedule);
        return schedule.getTrain().getTotalSeats() - confirmed;
    }

    private Schedule buildSchedule(UUID trainId, UUID routeId,
                                   LocalDateTime departureTime, LocalDateTime arrivalTime){
        Train train = trainService.findById(trainId);
        Route route = routeService.findById(routeId);

        Schedule schedule = new Schedule();
        schedule.setTrain(train);
        schedule.setRoute(route);
        schedule.setDepartureTime(departureTime);
        schedule.setArrivalTime(arrivalTime);
        schedule.setDelayMinutes(0);
        schedule.setStatus(ScheduleStatus.ON_TIME);

        return schedule;
    }
}