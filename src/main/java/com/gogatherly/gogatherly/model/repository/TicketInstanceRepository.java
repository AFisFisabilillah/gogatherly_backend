package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.entity.TicketInstance;
import com.gogatherly.gogatherly.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketInstanceRepository extends JpaRepository<TicketInstance, String> {
    Optional<TicketInstance> findByIdAndTicket_Event(String id, Event event);

    Page<TicketInstance> findByTicket_Event(Event event, Pageable pageable);


}