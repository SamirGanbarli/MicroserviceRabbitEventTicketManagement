package com.ticket.application.PaymenyService.domain.model;

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

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment", schema = "public")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String owner; // Reference to the user who made the payment

    @Column(nullable = false)
    private UUID eventId; // Reference to the event

    @Column(nullable = false)
    private Double amount; // Payment amount

    @Column(nullable = false)
    private String paymentStatus; // e.g., COMPLETED, REFUNDED

    @Column(nullable = false)
    private LocalDateTime paymentDate;
}
