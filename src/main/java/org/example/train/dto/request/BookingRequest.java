package org.example.train.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingRequest(

        @NotNull (message = "Person ID is required")
        UUID personId,

        @NotNull(message = "Schedule ID is required")
        UUID scheduleId,

        @NotNull(message = "From station ID is required")
        UUID fromStationId,

        @NotNull(message = "To station ID is required")
        UUID toStationId,

        @Min(value = 1, message = "Must book at least one seat")
        int numSeats,

        @NotNull(message = "Price per seat is required")
        BigDecimal pricePerSeat
) {}