package com.ticket.application.TicketService.service;

import com.ticket.application.TicketService.domain.model.Event;
import com.ticket.application.TicketService.domain.model.Ticket;
import com.ticket.application.TicketService.repository.TicketRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventConsumer {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    public EventConsumer (TicketRepository ticketRepository,TicketService ticketService) {
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
    }

    // Listen for messages from the event-created queue
    @RabbitListener(queues = "${spring.rabbitmq.queue.event-created}")
    public void handleEventCreated(Event event) {

        try {

            System.out.println("Received event: " + event.getName());
            // Create initial tickets for the event
            for (int i = 0; i < event.getAvailableTickets(); i++) {
                Ticket ticket = new Ticket();
                ticket.setOwner(null);
                ticket.setBookingDate(null);
                ticket.setEventId(event.getId());
                ticket.setStatus("AVAILABLE");
                ticket.setPrice(calculateTicketPrice(event)); // Add a pricing strategy if needed
                ticketRepository.save(ticket);
            }

            System.out.println("Tickets created for event: " + event.getName());

    } catch (Exception e) {
        throw e; // Re-throw exception if the message should be retried
    }
    }

    @RabbitListener(queues = "event-deleted-queue")
    public void handleEventDeleted(String eventId) {
        try {
            UUID eventUUID = UUID.fromString(eventId);
            ticketService.deleteTicketsByEvent(eventUUID);
            System.out.println("Tickets deleted for event: " + eventId);
        } catch (Exception e) {
            System.err.println("Failed to delete tickets for event: " + eventId);
        }
    }

    // Example method to calculate ticket price (can be customized)
    private Double calculateTicketPrice(Event event) {
        // Base pricing logic; this can be event-specific
        return 50.0; // Default price
    }
}