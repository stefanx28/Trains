package org.example.train.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        StationResponse fromStation,
        StationResponse toStation,
        int seatNumber,
        BigDecimal price,
        ScheduleResponse schedule
) {}