package com.ticket.application.TicketService.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tickets", schema = "public")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private UUID eventId; // Reference to the event

    @Column()
    private String owner; // Reference to the user who booked the ticket

    @Column()
    private LocalDateTime bookingDate;

    @Column()
    private String status; // e.g., AVAILABLE, BOOKED

    @Column()
    private Double price;
}
