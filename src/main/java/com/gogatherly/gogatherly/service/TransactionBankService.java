package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.TransactionBank;
import com.gogatherly.gogatherly.model.repository.TransactionBankRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class TransactionBankService {

    private final TransactionBankRepository transactionBankRepository;

    public TransactionBankService(TransactionBankRepository transactionBankRepository) {
        this.transactionBankRepository = transactionBankRepository;
    }

    public void createTransactionBank(Order order , Map<String,Object> request){
        TransactionBank transaction = new TransactionBank();
        List<Map<String, Object>> detailBank = ( List<Map<String, Object>>) request.get("va_numbers");

        transaction.setBank((String) detailBank.get(0).get("bank"));

        transaction.setVANumber((String)detailBank.get(0).get("va_number"));

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
        transactionBankRepository.save(transaction);
    }
}
