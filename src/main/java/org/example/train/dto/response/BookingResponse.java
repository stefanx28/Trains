package org.example.train.dto.response;

import org.example.train.model.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        PersonResponse person,
        LocalDateTime bookedAt,
        BookingStatus status,
        List<TicketResponse> tickets,
        BigDecimal totalPrice
) {}