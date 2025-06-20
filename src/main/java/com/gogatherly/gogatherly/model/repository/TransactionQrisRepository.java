package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.TransactionQris;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionQrisRepository extends JpaRepository<TransactionQris, String> {
}