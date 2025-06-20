package com.gogatherly.gogatherly.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gogatherly.gogatherly.dto.OrderRequest;
import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.OrderDetail;
import com.gogatherly.gogatherly.model.entity.Ticket;
import com.gogatherly.gogatherly.model.repository.OrderRepository;
import com.gogatherly.gogatherly.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.MailParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionCreditCardService transactionCreditCardService;
    @Autowired
    private TransactionQrisService transactionQrisService;
    @Autowired
    private TransactionEWalletService transactionEWalletService;
    @Autowired
    private TransactionBankService transactionBankService;
    @PostMapping(
            path = "/order",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Map<String , Object>> getToken(@RequestBody OrderRequest request) throws JsonProcessingException {
        String token = orderService.createToken(request);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        return WebResponse
                .<Map<String, Object>>builder()
                .status("success")
                .message("success create order")
                .data(response)
                .build();
    }

    @PostMapping(
            path = "/order/notification",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<Map<String, String>> notificationayment(@RequestBody Map<String, Object> request){
        String orderId = (String) request.get("order_id");
        if(orderId ==  null){
            return  WebResponse
                    .<Map<String, String>>builder()
                    .message("success ")
                    .status("success get notification of midtrans")
                    .build();
        }
        if(orderId.startsWith("payment_notif_test") ){
            return  WebResponse
                    .<Map<String, String>>builder()
                    .message("success ")
                    .status("success get notification of midtrans")
                    .build();
        }
        log.info("notification payment");
        String transactionStatus = (String) request.get("transaction_status");
        Order order = orderRepository.findById((String) request.get("order_id")).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found order"));
        if(transactionStatus.equals("capture") || transactionStatus.equals("settlement")){
            orderService.updateStatusOrderToSuccess(order);
            log.info("payment : success");
        }else if(transactionStatus.equals("pending")){
            orderService.updateStatusOrderToPending(order);
            log.info("payment : pending");
        }else if(transactionStatus.equals("deny")||transactionStatus.equals("cancel")||transactionStatus.equals("expire")||transactionStatus.equals("failur")){
            log.info("payment : failur");
            orderService.updateStatusOrderToFailure(order);
            List<OrderDetail> orderDetails = order.getOrderDetails();
            for(OrderDetail orderDetail : orderDetails){
                Ticket ticket = orderDetail.getTicket();
                ticket.setQuantity(ticket.getQuantity()+orderDetail.getQuantity());
            }
        }else{
            orderService.updateStatusOrderToFailure(order);
            List<OrderDetail> orderDetails = order.getOrderDetails();
            for(OrderDetail orderDetail : orderDetails){
                Ticket ticket = orderDetail.getTicket();
                ticket.setQuantity(ticket.getQuantity()+orderDetail.getQuantity());
            }
        }

        String paymentType = (String) request.get("payment_type");

        switch (paymentType){
            case "credit_card":
                transactionCreditCardService.createTransactionCreditCard(order, request);
                break;
            case "bank_transfer":
                transactionBankService.createTransactionBank(order,request);
                break;
            case "qris" :
                transactionQrisService.createTransactionQris(order,request);
                break;
            default:
                transactionEWalletService.createTransactionEWaller(order,request);
        }




        return WebResponse
                .<Map<String, String>>builder()
                .message("success ")
                .status("success get notification of midtrans")
                .data(new HashMap<>(Map.of("success", "success")))
                .build();
    }
}
