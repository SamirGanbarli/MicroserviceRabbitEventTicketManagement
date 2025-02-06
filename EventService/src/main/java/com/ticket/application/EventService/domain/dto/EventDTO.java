package com.ticket.application.EventService.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventDTO {

    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Integer availableTickets;
    private Double ticketPrice;
    private String organizer;
}
