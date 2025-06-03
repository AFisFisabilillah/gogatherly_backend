package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.EventManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventManagerRepository extends JpaRepository<EventManager, Integer> {

}
