package com.ticket.application.PaymenyService.domain;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue ticketBookedQueue() {
        return new Queue("ticket-booked-queue", true);
    }
    @Bean
    public Queue ticketRefundedQueue() {
        return new Queue("ticket-refunded-queue", true);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return new Queue("payment-request-queue", true);
    }

    @Bean
    public Queue paymentSuccessfulQueue() {
        return new Queue("payment-successful-queue", true);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue eventDeletedQueue() {
        return new Queue("event-deleted-queue", true);
    }
}
