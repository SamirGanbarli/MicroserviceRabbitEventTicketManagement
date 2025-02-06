package com.ticket.application.PaymenyService.controller;

import com.ticket.application.PaymenyService.domain.dto.PaymentDTO;
import com.ticket.application.PaymenyService.domain.model.Payment;
import com.ticket.application.PaymenyService.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

}