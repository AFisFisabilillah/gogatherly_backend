package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByStatusAndExpiredAtBefore(String status, LocalDateTime expiredAt);
}
