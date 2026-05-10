package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.model.Booking;
import org.example.train.model.Person;
import org.example.train.model.Ticket;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendConfirmation(Person person, Booking booking, List<Ticket> tickets) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(person.getEmail());
        message.setSubject("Booking Confirmed – #" + booking.getId());
        message.setText(buildConfirmationBody(person, booking, tickets));
        mailSender.send(message);
    }

    public void sendDelayNotification(Booking booking, int delayMinutes) {
        Person person = booking.getPerson();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(person.getEmail());
        message.setSubject("Train Delay Notice – Booking #" + booking.getId());
        message.setText(buildDelayBody(person, booking, delayMinutes));
        mailSender.send(message);
    }

    private String buildConfirmationBody(Person person, Booking booking, List<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(person.getName()).append(",\n\n");
        sb.append("Your booking has been confirmed.\n");
        sb.append("Booking ID: ").append(booking.getId()).append("\n\n");
        sb.append("Tickets:\n");

        for (Ticket ticket : tickets) {
            sb.append("  Seat ").append(ticket.getSeatNumber())
                    .append(" | ").append(ticket.getFromStation().getName())
                    .append(" → ").append(ticket.getToStation().getName())
                    .append(" | Train: ").append(ticket.getSchedule().getTrain().getTrainNumber())
                    .append(" | Departure: ").append(ticket.getSchedule().getDepartureTime())
                    .append(" | Price: ").append(ticket.getPrice()).append("\n");
        }

        sb.append("\nThank you for travelling with us.");
        return sb.toString();
    }

    private String buildDelayBody(Person person, Booking booking, int delayMinutes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(person.getName()).append(",\n\n");
        sb.append("We regret to inform you that your train has been delayed.\n\n");
        sb.append("Booking ID: ").append(booking.getId()).append("\n");
        sb.append("Delay: ").append(delayMinutes).append(" minutes\n\n");
        sb.append("We apologise for the inconvenience.");
        return sb.toString();
    }
}