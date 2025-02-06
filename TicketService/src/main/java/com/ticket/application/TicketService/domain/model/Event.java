package com.ticket.application.TicketService.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Event {
    private UUID id;

    private String name;

    private String description;

    private LocalDateTime eventDate;

    private String location;

    private Integer availableTickets;

    private Double ticketPrice;

    private String organizer;
}
