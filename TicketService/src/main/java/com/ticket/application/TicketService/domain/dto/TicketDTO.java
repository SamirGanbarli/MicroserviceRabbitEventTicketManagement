package com.ticket.application.TicketService.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TicketDTO {
    private UUID eventId;
    private String owner;
    private Double price;
}

