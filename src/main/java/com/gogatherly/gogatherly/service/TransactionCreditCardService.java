package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.TransactionCreditCard;
import com.gogatherly.gogatherly.model.repository.TransactionCreditCardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TransactionCreditCardService {

    private final TransactionCreditCardRepository transactionCreditCardRepository;

    public TransactionCreditCardService(TransactionCreditCardRepository transactionCreditCardRepository) {
        this.transactionCreditCardRepository = transactionCreditCardRepository;
    }

    public void createTransactionCreditCard(Order order, Map<String, Object> request){
        TransactionCreditCard transaction = new TransactionCreditCard();

        transaction.setMaskedCard("masked_card");

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

        transactionCreditCardRepository.save(transaction);
    }
}
