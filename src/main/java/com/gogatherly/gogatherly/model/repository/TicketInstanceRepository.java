package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.entity.TicketInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketInstanceRepository extends JpaRepository<TicketInstance, String> {
    Optional<TicketInstance> findByIdAndTicket_Event(String id, Event event);

}