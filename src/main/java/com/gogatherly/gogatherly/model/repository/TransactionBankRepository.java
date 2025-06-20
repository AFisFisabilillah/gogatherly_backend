package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.TransactionBank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionBankRepository extends JpaRepository<TransactionBank, String> {
}