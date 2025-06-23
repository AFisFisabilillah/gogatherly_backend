package com.gogatherly.gogatherly.scheduler;

import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.OrderDetail;
import com.gogatherly.gogatherly.model.entity.Ticket;
import com.gogatherly.gogatherly.model.repository.OrderRepository;
import com.gogatherly.gogatherly.model.repository.TicketRepository;
import com.gogatherly.gogatherly.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PaymentFailedJob {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Scheduled(timeUnit = TimeUnit.MINUTES,fixedDelay = 1,initialDelay = 1)
    @Transactional
    public void handleExpiredPendingOrder (){
        log.info("Running Scheduled task : handleExpiredPendingOrder at {} ",LocalDateTime.now().toString());
        List<Order> orders = orderRepository.findByStatusAndExpiredAtBefore("PENDING", LocalDateTime.now());
        if(!orders.isEmpty()){
            log.info("found {} order with status pending expired", orders.size());
            for (Order order : orders){
                order.setStatus("FAILED");
                List<OrderDetail> orderDetails = order.getOrderDetails();
                for (OrderDetail orderDetail : orderDetails ){
                    Ticket ticket = orderDetail.getTicket();
                    ticket.setQuantity(ticket.getQuantity()+orderDetail.getQuantity());
                    ticketRepository.save(ticket);
                }
                orderRepository.save(order);

                log.info("Succes Order {} cahnge status to FAILED ", order.getId());
            }
        }else{
            log.info("not found Order pending and expired");
        }
    }
}
