package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.MetaData;
import com.gogatherly.gogatherly.dto.TicketInstanceResponse;
import com.gogatherly.gogatherly.dto.WebResponseList;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.*;
import com.gogatherly.gogatherly.model.repository.EventRepository;
import com.gogatherly.gogatherly.model.repository.TicketInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class TicketInstanceService {
    @Autowired
    private EventRepository eventRepository;
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

    @Async
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


    public WebResponseList<List<TicketInstanceResponse>> getAllTciketInstance(Integer page , Integer size, Integer eventId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Event event = eventRepository.findByIdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "event Not found"));

        LinkedList<TicketInstanceResponse> responses = new LinkedList<>();
        Page<TicketInstance> tickets = ticketInstanceRepository.findByTicket_Event(event, pageRequest);

        for(TicketInstance ticket: tickets.getContent()){
            TicketInstanceResponse response = new TicketInstanceResponse();
            response.setTicketId(ticket.getId());
            response.setUser(ticket.getUser().getName());
            response.setNik(ticket.getUser().getNik());
            response.setOrderId(ticket.getOrder().getId());
            response.setPhoneNumber(ticket.getUser().getPhoneNumber());
            response.setIsUsed(ticket.getUsed());
            response.setTicketType(ticket.getTicket().getTitle());
            response.setUsedAt(ticket.getUsedAt());
            responses.add(response);
        }
        MetaData metaData = new MetaData();
        metaData.setHasPrevious(tickets.hasPrevious());
        metaData.setHasNext(tickets.hasNext());
        metaData.setTotalPages(tickets.getTotalPages());
        metaData.setTotalElements(tickets.getTotalElements());
        metaData.setPage(tickets.getNumber());
        metaData.setSize(tickets.getSize());
        return WebResponseList
                .<List<TicketInstanceResponse>>builder()
                .meta(metaData)
                .message("Success get user yang membeli ticket")
                .status("success")
                .data(responses)
                .build();
    }
}
