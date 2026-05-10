package org.example.train.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleRequest(

        @NotNull(message = "Train ID is required")
        UUID trainId,

        @NotNull(message = "Route ID is required")
        UUID routeId,

        @NotNull(message = "Departure time is required")
        @Future(message = "Departure time must be in the future")
        LocalDateTime departureTime,

        @NotNull(message = "Arrival time is required")
        LocalDateTime arrivalTime
) {}