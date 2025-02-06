package com.ticket.application.EventService.domain;

import com.ticket.application.EventService.domain.model.Event;
import com.ticket.application.EventService.service.EventService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class EventConsumer {

    private final EventService eventService;

    public EventConsumer(EventService eventService) {
        this.eventService = eventService;
    }

    @RabbitListener(queues = "ticket-booked-queue")
    public void handleTicketBooked(String eventIdStr) {
        try {
            UUID eventId = UUID.fromString(eventIdStr);
            eventService.decreaseAvailableTickets(eventId);
            System.out.println("EventService: Decreased available tickets for event: " + eventId);
        } catch (Exception e) {
            System.err.println("Failed to update event ticket count: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "ticket-refunded-queue")
    public void handleRefundedTicket(UUID eventId) {
        try {
            eventService.refundTicket(eventId);
            System.out.println("EventService:Refund a ticket of  " + eventId);
        } catch (Exception e) {
            System.err.println("Failed to update event ticket count: " + e.getMessage());
        }
    }
}
