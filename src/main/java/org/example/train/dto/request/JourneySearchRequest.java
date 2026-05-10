package org.example.train.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record JourneySearchRequest(

        @NotNull(message = "From station ID is required")
        UUID fromStationId,

        @NotNull(message = "To station ID is required")
        UUID toStationId,

        @NotNull(message = "Departure date is required")
        LocalDateTime after
) {}