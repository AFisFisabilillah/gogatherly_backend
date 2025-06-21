package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.TicketInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketInstanceRepository extends JpaRepository<TicketInstance, String> {
}