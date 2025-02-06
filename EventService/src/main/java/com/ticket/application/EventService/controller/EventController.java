package com.ticket.application.EventService.controller;

import com.ticket.application.EventService.domain.JwtTokenProvider;
import com.ticket.application.EventService.domain.dto.EventDTO;
import com.ticket.application.EventService.domain.model.Event;
import com.ticket.application.EventService.service.EventService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestHeader("Authorization") String authHeader, @RequestBody Event event) {
        // Extract the token from the header
        String token = authHeader.replace("Bearer ", "");

        // Validate the token
        if (!jwtTokenProvider.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Extract claims or user-specific details (optional)
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);

        // Check if the user has the ADMIN role
        String role = claims.get("role", String.class);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to create events");
        }
        //event.setId(UUID.randomUUID());
        event.setOrganizer(claims.getSubject());

        // Proceed with event creation
        EventDTO createdEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID eventId,
            @RequestBody Event updatedEvent) {

        // Extract and validate the token
        String token = authHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Extract claims
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String role = claims.get("role", String.class);

        // Only admins can update events
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update events");
        }

        // Retrieve existing event
        Optional<Event> existingEvent = eventService.findEventById(eventId);
        if (existingEvent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        // Only allow updating name, description, date, location, and price
        existingEvent.get().setName(updatedEvent.getName());
        existingEvent.get().setDescription(updatedEvent.getDescription());
        existingEvent.get().setEventDate(updatedEvent.getEventDate());
        existingEvent.get().setLocation(updatedEvent.getLocation());
        existingEvent.get().setTicketPrice(updatedEvent.getTicketPrice());

        // Save the updated event
        eventService.updateEvent(existingEvent.get());

        return ResponseEntity.ok(existingEvent);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID eventId) {

        // Extract and validate the token
        String token = authHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Extract claims
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String role = claims.get("role", String.class);

        // Only admins can delete events
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to delete events");
        }

        // Call service to delete event
        boolean isDeleted = eventService.deleteEvent(eventId);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        return ResponseEntity.ok("Event deleted successfully.");
    }

    @GetMapping
    public ResponseEntity<?> getEvents(
            @RequestHeader("Authorization") String authHeader) {
        // Call service to delete event
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable UUID eventId) {
        // Call service to delete event
        Optional<Event> event = eventService.findEventById(eventId);
        return ResponseEntity.ok(event);
    }

}


