package com.ticket.application.EventService.repository;

import com.ticket.application.EventService.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findByLocationAndEventDate(String location, LocalDateTime eventDate);

}
