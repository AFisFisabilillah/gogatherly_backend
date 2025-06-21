package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.entity.Order;
import com.gogatherly.gogatherly.model.entity.OrderDetail;
import com.gogatherly.gogatherly.model.entity.TicketInstance;
import com.gogatherly.gogatherly.model.repository.TicketInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class TicketInstanceService {
    @Value("${hostname.url}")
    private String hostname;
    @Autowired
    private TicketInstanceRepository ticketInstanceRepository;
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private EmailService emailService;

    public void createTicketInstance(Order order){
        if(!order.getStatus().equals("SUCCESS")){
            throw  new ErrorResponseException(HttpStatus.PAYMENT_REQUIRED,"error", "you order not success");
        }

        for (OrderDetail orderDetail : order.getOrderDetails()){
            String id = UUID.randomUUID().toString();

            TicketInstance ticketInstance = new TicketInstance();
            ticketInstance.setUser(order.getUser());
            ticketInstance.setOrder(order);
            ticketInstance.setTicket(orderDetail.getTicket());
            ticketInstance.setId(id);
            String url= null;
            try {
                 url = qrCodeService.createQrCode(id);
            }catch (Exception e){
                log.info(e.getMessage());
            }

            ticketInstance.setUsed(false);
            ticketInstance.setQrCodeUrl(hostname+"/public/ticket_qrcode/"+url);
            ticketInstanceRepository.save(ticketInstance);


            sendEmailTicket(ticketInstance);

        }

    }

    public void sendEmailTicket(TicketInstance ticket){
        Context context = new Context();
        Event event =ticket.getTicket().getEvent();
        context.setVariable("eventName",event.getTitle());


        context.setVariable("bannerUrl",hostname+"/public/banner/"+event.getBanner());

        log.info("banner : {}",event.getBanner());
        context.setVariable("buyerName",ticket.getUser().getName());

        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd MMM yyyy");
        context.setVariable("eventDate",event.getStartEvent().format(date) );

        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        context.setVariable("eventTime", event.getStartEvent().format(time));

        context.setVariable("eventLocation", event.getLocation().getProvince() + " "+event.getLocation().getCity());

        log.info("barcode url : {}",ticket.getQrCodeUrl());
        context.setVariable("barcodeUrl", ticket.getQrCodeUrl());

        String responseEmail = templateEngine.process("ticket", context);

        log.info("email message {}",responseEmail);
        try {
            emailService.sendEmail(ticket.getUser().getEmail(),responseEmail,"ticket elektronik");
        }catch (Exception e){
            throw  new ErrorResponseException(HttpStatus.OK, "error", "error email");
        }
    }
}
