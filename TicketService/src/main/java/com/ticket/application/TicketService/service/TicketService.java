package com.ticket.application.TicketService.service;

import com.ticket.application.TicketService.domain.dto.TicketDTO;
import com.ticket.application.TicketService.domain.model.Event;
import com.ticket.application.TicketService.domain.model.Ticket;
import com.ticket.application.TicketService.repository.TicketRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.ticket-booked.queue}")
    private String ticketBookedQueue;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public void requestPayment(UUID eventId, String username, Double amount, UUID ticketId) {
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("eventId", eventId);
        paymentRequest.put("username", username);
        paymentRequest.put("amount", amount);
        paymentRequest.put("ticketId", ticketId);

        // Send payment request to RabbitMQ
        rabbitTemplate.convertAndSend("payment-request-queue", paymentRequest);
        System.out.println("🔹 Payment request sent for event: " + eventId + " by user: " + username);
    }

    @RabbitListener(queues = "payment-successful-queue")
    public void handlePaymentSuccess(Map<String, Object> paymentResponse) {
        UUID ticketId = UUID.fromString(paymentResponse.get("ticketId").toString());


        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setOwner(paymentResponse.get("username").toString());
            ticket.setStatus("BOOKED");
            ticketRepository.save(ticket);

            // Notify EventService to decrement available tickets
            sendTicketBookedEvent(optionalTicket.get().getEventId());

            System.out.println("Ticket successfully booked for user: " + ticket.getOwner());
        } else {
            System.out.println("Ticket not found for booking.");
        }
    }

    @Transactional // Ensure that booking is atomic
    public void bookTicket(TicketDTO ticketDto) {
        // Fetch available tickets for the event
        List<Ticket> tickets = ticketRepository.findByEventId(ticketDto.getEventId());

        // Find first available ticket
        Optional<Ticket> availableTicket = tickets.stream()
                .filter(ticket -> "AVAILABLE".equals(ticket.getStatus()))
                .findFirst();

        if (availableTicket.isEmpty()) {
            throw new IllegalStateException("No available tickets for this event.");
        }

        // Book the ticket
        Ticket ticketToBook = availableTicket.get();
        ticketToBook.setOwner(ticketDto.getOwner());
        ticketToBook.setStatus("BOOKED");

        requestPayment(ticketToBook.getEventId(), ticketDto.getOwner(),ticketToBook.getPrice(), ticketToBook.getId());
    }

    public Ticket refundTicket(UUID ticketId, String username) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);

        if (optionalTicket.isEmpty()) {
            throw new IllegalArgumentException("Ticket not found.");
        }

        Ticket ticket = optionalTicket.get();

        // Ensure that the requesting user is the owner of the ticket
        if (ticket.getOwner() == null || !ticket.getOwner().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this ticket.");
        }

        // Set the ticket status back to AVAILABLE
        ticket.setOwner(null);
        ticket.setStatus("AVAILABLE");

        Ticket updatedTicket = ticketRepository.save(ticket);

        // Notify EventService to increase the available ticket count
        publishRefundEvent(ticket.getEventId());

        return updatedTicket;
    }

    private void publishRefundEvent(UUID eventId) {
        rabbitTemplate.convertAndSend("ticket-refunded-queue", eventId);
        System.out.println("Refund event sent to RabbitMQ for event: " + eventId);
    }

    private void sendTicketBookedEvent(UUID eventId) {
        rabbitTemplate.convertAndSend(ticketBookedQueue, eventId.toString());
        System.out.println("Ticket booked message sent to EventService for event: " + eventId);
    }

    public void deleteTicketsByEvent(UUID eventId) {
        long count = ticketRepository.countByEventId(eventId);
        if (count == 0) {
            System.out.println("No tickets found for event ID: " + eventId);
            return; // Skip deletion if no tickets exist
        }

        //delete tickets one by one and notify the user if the ticket booked

        ticketRepository.deleteByEventId(eventId);
        System.out.println("Deleted " + count + " tickets for event ID: " + eventId);
    }

    public List<Ticket> getTicketsByOwner(String owner) {
        List<Ticket> tickets = ticketRepository.findByOwner(owner);
        return tickets;
    }


    public Optional<Ticket> getTicketById(UUID ticketId) {
        return ticketRepository.findById(ticketId);
    }
}
