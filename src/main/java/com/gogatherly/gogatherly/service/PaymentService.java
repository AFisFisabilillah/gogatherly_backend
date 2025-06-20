package com.gogatherly.gogatherly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.Ticket;
import com.gogatherly.gogatherly.model.entity.User;
import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;
import com.midtrans.service.impl.MidtransSnapApiImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PaymentService implements InitializingBean {

    @Value("${midtrans.client.key}")
    private String clientKey;
    @Value("${midtrans.url.notification}")
    private String urlNotification;
    @Value("${midtrans.server.key}")
    private String serverKey;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("menginisialisais bean payment service");
        Midtrans.serverKey = serverKey;
        Midtrans.clientKey = clientKey;
        Midtrans.isProduction = false;
        Midtrans.paymentOverrideNotification(urlNotification);
        log.info("url notification : {}", Midtrans.getPaymentAppendNotification());
    }

    public String createToken(Ticket ticket, User user , Integer quantity, String orderId  ) throws JsonProcessingException {
        Map<String, Object> request = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", String.valueOf(ticket.getPrice() * quantity));

        List< Map<String, Object>>  items = new LinkedList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", ticket.getId());
        item.put("quantity", quantity);
        item.put("price", ticket.getPrice());
        item.put("name", ticket.getTitle());
        item.put("brand", ticket.getEvent().getTitle());
        item.put("category", "ticket");
        item.put("merchant_name", ticket.getEvent().getUser().getName());
        items.add(item);

        Map<String, Object> customerDetails = new HashMap<>();

        customerDetails.put("first_name", user.getName());
        customerDetails.put("email", user.getEmail());
        customerDetails.put("phone", user.getPhoneNumber());

        Map<String, Object> pageExpired = new HashMap<>();
        pageExpired.put("duration", 15);
        pageExpired.put("unit", "minutes");

        Map<String, Object> expire = new HashMap<>();
        expire.put("duration", 15);
        expire.put("unit", "minutes");

        request.put("transaction_details", transactionDetails);
        request.put("item_details", items);
        request.put("customer_details", customerDetails);
        request.put("expiry", expire);
        request.put("page_expiry", pageExpired);

        String json = objectMapper.writeValueAsString(request);

        try {
            String token = SnapApi.createTransactionToken(request);
            return token;
        }catch (MidtransError exception){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", exception.getResponseBody());
        }
    }




}
