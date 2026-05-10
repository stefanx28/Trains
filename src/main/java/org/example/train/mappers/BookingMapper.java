package org.example.train.mappers;

import lombok.RequiredArgsConstructor;
import org.example.train.dto.response.*;
import org.example.train.model.Booking;
import org.example.train.model.Ticket;
import org.example.train.service.ScheduleService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ScheduleMapper scheduleMapper;

    public BookingResponse toResponse(Booking booking) {

        PersonResponse personResponse = new PersonResponse(
                booking.getPerson().getId(),
                booking.getPerson().getName(),
                booking.getPerson().getEmail(),
                booking.getPerson().getRole()
        );


        List<TicketResponse> ticketResponses = booking.getTickets().stream()
                .map(this::toTicketResponse)
                .toList();

        // Calculate Total
        BigDecimal total = booking.getTickets().stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BookingResponse(
                booking.getId(),
                personResponse,
                booking.getBookedAt(),
                booking.getStatus(),
                ticketResponses,
                total
        );
    }

    private TicketResponse toTicketResponse(Ticket ticket) {
        StationResponse from = new StationResponse(
                ticket.getFromStation().getId(),
                ticket.getFromStation().getName(),
                ticket.getFromStation().getCity(),
                ticket.getFromStation().getCode()
        );

        StationResponse to = new StationResponse(
                ticket.getToStation().getId(),
                ticket.getToStation().getName(),
                ticket.getToStation().getCity(),
                ticket.getToStation().getCode()
        );


        ScheduleResponse scheduleResponse = scheduleMapper.toResponse(ticket.getSchedule());

        return new TicketResponse(
                ticket.getId(),
                from,
                to,
                ticket.getSeatNumber(),
                ticket.getPrice(),
                scheduleResponse
        );
    }
}