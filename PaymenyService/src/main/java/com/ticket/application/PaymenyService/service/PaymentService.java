package com.ticket.application.PaymenyService.service;

import com.ticket.application.PaymenyService.domain.dto.PaymentDTO;
import com.ticket.application.PaymenyService.domain.model.Payment;
import com.ticket.application.PaymenyService.repository.PaymentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;

    public PaymentService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "payment-request-queue")
    public void processPayment(Map<String, Object> paymentRequest) {
        UUID eventId = UUID.fromString(paymentRequest.get("eventId").toString());
        String username = paymentRequest.get("username").toString();
        Double amount = Double.parseDouble(paymentRequest.get("amount").toString());
        UUID ticketId = UUID.fromString(paymentRequest.get("ticketId").toString());

        System.out.println("Processing payment for user: " + username + ", event: " + eventId + ", amount: $" + amount);

        // Simulate fake payment processing
        boolean isPaymentSuccessful = fakePaymentProcessing(username, amount);

        Map<String, Object> paymentResponse = new HashMap<>();
        paymentResponse.put("ticketId", ticketId);
        paymentResponse.put("username", username);
        paymentResponse.put("paymentStatus", isPaymentSuccessful ? "COMPLETED" : "FAILED");

        if (isPaymentSuccessful) {
            // Send success message to RabbitMQ
            rabbitTemplate.convertAndSend("payment-successful-queue", paymentResponse);
            System.out.println("Payment successful for event: " + eventId + ", user: " + username);
        } else {
            System.out.println("Payment failed for event: " + eventId + ", user: " + username);
        }
    }

    private boolean fakePaymentProcessing(String username, Double amount) {
        // Simulate 90% success rate
        return new Random().nextInt(100) < 90;
    }
}