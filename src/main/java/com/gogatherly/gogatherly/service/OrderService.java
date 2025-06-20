package com.gogatherly.gogatherly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gogatherly.gogatherly.dto.OrderRequest;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.OrderDetail;
import com.gogatherly.gogatherly.model.entity.Ticket;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.OrderDetailRepository;
import com.gogatherly.gogatherly.model.repository.OrderRepository;
import com.gogatherly.gogatherly.model.repository.TicketRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private Validator validator;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentService paymentService;

    public String  createToken(OrderRequest request) throws JsonProcessingException {
        Set<ConstraintViolation<OrderRequest>> validate = validator.validate(request);
        if(validate.size() > 0 ){
            throw new ConstraintViolationException(validate);
        }

        request.setQuantity(1L);

        Ticket ticket = ticketRepository.findById(Integer.valueOf(request.getTicketId())).orElse(null);

        if(LocalDateTime.now().isBefore(ticket.getStartTicket())){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "Ticket purchase is not available yet. Please wait until the sale period begins.");

        }
        if(LocalDateTime.now().isAfter(ticket.getEndTicket())){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "Ticket sales have ended. This event is no longer available.");
        }

        if (ticket.getQuantity() < 1){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "Ticket are sold out.");
        }
//        mendapatka user dari security holder
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//        Mmebuat order id
        String orderId = UUID.randomUUID().toString();



//        membaut instance order
        Order order = new Order();
        order.setId(orderId);
        order.setOrderDate(LocalDateTime.now());
        order.setExpiredAt(LocalDateTime.now().plusMinutes(15));
        order.setUser(user);
        order.setAmount(ticket.getPrice() * request.getQuantity());
        order.setStatus("PENDING");
        orderRepository.save(order);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setTotal(ticket.getPrice());
        orderDetail.setQuantity(request.getQuantity().intValue());
        orderDetail.setTicket(ticket);
        orderDetailRepository.save(orderDetail);

        String token = paymentService.createToken(ticket, user, request.getQuantity().intValue(), orderId);

        log.info("ticket quantity sebelum di kurangi : {}", ticket.getQuantity());
        ticket.setQuantity(ticket.getQuantity() - request.getQuantity());
        ticketRepository.save(ticket);
        log.info("ticket sesudah di kurangi : {}", ticket.getQuantity());
        return token;

    }

    public void updateStatusOrderToSuccess(Order order){
        order.setStatus("SUCCESS");
        orderRepository.save(order);
    }

    public void updateStatusOrderToPending(Order order){
        order.setStatus("PENDING");
        orderRepository.save(order);
    }

    public void updateStatusOrderToFailure(Order order){
        order.setStatus("FAILURE");
        orderRepository.save(order);
    }
}
