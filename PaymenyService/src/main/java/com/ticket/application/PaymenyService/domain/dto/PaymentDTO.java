package com.ticket.application.PaymenyService.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentDTO {

    private UUID userId;
    private UUID eventId;
    private Double amount;
}
