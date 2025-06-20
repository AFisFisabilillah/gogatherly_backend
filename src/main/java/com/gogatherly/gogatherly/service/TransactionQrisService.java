package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.TransactionQris;
import com.gogatherly.gogatherly.model.repository.TransactionQrisRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TransactionQrisService {

    private final TransactionQrisRepository transactionQrisRepository;

    public TransactionQrisService(TransactionQrisRepository transactionQrisRepository) {
        this.transactionQrisRepository = transactionQrisRepository;
    }

    public void createTransactionQris(Order order, Map<String, Object> request){
        TransactionQris transaction = new TransactionQris();

        transaction.setIssuer((String)request.get("issuer"));
        transaction.setAcquirer((String)request.get("acquirer"));

        transaction.setId((String)request.get("transaction_id"));

        transaction.setTransactionStatus((String)request.get("transaction_status"));

//        Time
        String dateStr = (String) request.get("transaction_time");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
        transaction.setTransactionTime(dateTime);

        transaction.setOrder(order);

        Double grossAmount = Double.parseDouble((String) request.get("gross_amount"));
        transaction.setGrossAmount(grossAmount.longValue());

        transaction.setFraudStatus((String) request.get("fraud_status"));

        transactionQrisRepository.save(transaction);
    }
}
