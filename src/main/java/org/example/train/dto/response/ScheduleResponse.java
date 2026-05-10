package org.example.train.dto.response;

import org.example.train.model.enums.ScheduleStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleResponse(
        UUID id,
        TrainResponse train,
        RouteResponse route,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        int delayMinutes,
        ScheduleStatus status,
        int availableSeats
) {}