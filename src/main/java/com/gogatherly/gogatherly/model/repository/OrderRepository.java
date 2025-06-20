package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
