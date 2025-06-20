package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    private void createTransaction(Order order){

    }
}
