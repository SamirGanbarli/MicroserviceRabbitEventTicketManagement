package com.ticket.application.TicketService.controller;

import com.ticket.application.TicketService.domain.JwtTokenProvider;
import com.ticket.application.TicketService.domain.dto.TicketDTO;
import com.ticket.application.TicketService.domain.model.Ticket;
import com.ticket.application.TicketService.service.TicketService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private  TicketService ticketService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestHeader("Authorization") String authHeader, @RequestBody TicketDTO ticketDto) {
        // Extract the token from the header
        String token = authHeader.replace("Bearer ", "");

        // Validate the token
        if (!jwtTokenProvider.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Extract claims
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);

        // Check if the user has the "USER" role
        String role = claims.get("role", String.class);
        if (!"USER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only users can book a ticket");
        }

        // Set owner as the logged-in user
        ticketDto.setOwner(claims.getSubject());

        ticketService.bookTicket(ticketDto);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundTicket(@RequestHeader("Authorization") String authHeader, @RequestBody UUID ticketId) {
        // Extract token from Authorization header
        String token = authHeader.replace("Bearer ", "");

        // Validate the token
        if (!jwtTokenProvider.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Extract claims
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String username = claims.getSubject(); // Get username from token

        // Check if the user has the "USER" role
        String role = claims.get("role", String.class);
        if (!"USER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only users can book a ticket");
        }

        try {
            Ticket refundedTicket = ticketService.refundTicket(ticketId, username);
            return ResponseEntity.ok(refundedTicket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID ticketId) {
        return ticketService.getTicketById(ticketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/my-tickets")
    public ResponseEntity<?> getMyTickets(@RequestHeader("Authorization") String authHeader) {
        // Extract token
        String token = authHeader.replace("Bearer ", "");

        // Validate the token
        if (!jwtTokenProvider.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Extract the username from the token
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String username = claims.getSubject();

        // Fetch tickets for the user
        List<Ticket> myTickets = ticketService.getTicketsByOwner(username);

        return ResponseEntity.ok(myTickets);
    }

}