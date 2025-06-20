package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.TransactionEWallet;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TransactionEWalletService {
    public void createTransactionEWaller(Order order, Map<String, Object> request){
        TransactionEWallet transaction = new TransactionEWallet();

        transaction.setEWalletName((String)request.get("payment_type"));

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
    }
}
