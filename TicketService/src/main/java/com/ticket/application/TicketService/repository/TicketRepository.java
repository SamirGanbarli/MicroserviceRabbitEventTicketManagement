package com.ticket.application.TicketService.repository;

import com.ticket.application.TicketService.domain.model.Ticket;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByEventId(UUID eventId);
    List<Ticket> findByOwner(String owner);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Ticket t WHERE t.eventId = :eventId")
    void deleteByEventId(@Param("eventId") UUID eventId);

    long countByEventId(UUID eventId);
}