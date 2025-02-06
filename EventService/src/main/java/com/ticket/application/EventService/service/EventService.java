package com.ticket.application.EventService.service;

import com.ticket.application.EventService.domain.dto.EventDTO;
import com.ticket.application.EventService.domain.model.Event;
import com.ticket.application.EventService.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.event-deleted.queue}")
    private String eventDeletedQueue;

    @Value("${spring.rabbitmq.queue.event-created}")
    private String eventCreatedQueue;

    public Optional<Event> findEventById(UUID id) {
        return eventRepository.findById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }


    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public boolean deleteEvent(UUID eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            // Step 1: Publish "Event Deleted" message to RabbitMQ
            rabbitTemplate.convertAndSend(eventDeletedQueue, eventId.toString());

            // Step 2: Delete event from DB
            eventRepository.deleteById(eventId);
            return true;
        }

        return false;
    }


    public EventDTO createEvent(Event event) {
        // Save the event to the database
        if (isDuplicateEvent(event.getLocation(), event.getEventDate())) {
            throw new IllegalArgumentException("An event with the same location and time already exists.");
        }

        eventRepository.save(event);

        // Publish the event to RabbitMQ
        publishEventToQueue(event);

        // Map the saved event to an EventDTO and return
        return mapToEventDTO(event);
    }

    @Transactional
    public void decreaseAvailableTickets(UUID eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();

            if (event.getAvailableTickets() > 0) {
                event.setAvailableTickets(event.getAvailableTickets() - 1);
                eventRepository.save(event);
            } else {
                throw new IllegalStateException("No available tickets left for event.");
            }
        } else {
            throw new IllegalStateException("Event not found.");
        }
    }

    @Transactional
    public void refundTicket(UUID eventId){
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setAvailableTickets(event.getAvailableTickets() + 1);
            eventRepository.save(event);
            System.out.println("Updated event available tickets for event: " + eventId);
        } else {
            System.out.println("Event not found for refund update.");
        }
    }


    // Method to check for duplicate events
    public boolean isDuplicateEvent(String location, LocalDateTime eventDate) {
        Optional<Event> existingEvent = eventRepository.findByLocationAndEventDate(location, eventDate);
        return existingEvent.isPresent();
    }
    private void publishEventToQueue(Event event) {
        rabbitTemplate.convertAndSend(eventCreatedQueue, event);
        log.info("Event published to RabbitMQ: {}", event.getName());
    }

    // Manual mapping method to replace ModelMapper
    private EventDTO mapToEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setName(event.getName());
        eventDTO.setLocation(event.getLocation());
        eventDTO.setAvailableTickets(event.getAvailableTickets());
        eventDTO.setOrganizer(event.getOrganizer());
        return eventDTO;
    }
}
