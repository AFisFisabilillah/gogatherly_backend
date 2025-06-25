package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.*;
import com.gogatherly.gogatherly.model.entity.Scanner;
import com.gogatherly.gogatherly.model.entity.TicketInstance;
import com.gogatherly.gogatherly.service.EventManagerService;
import com.gogatherly.gogatherly.service.QrCodeService;
import com.gogatherly.gogatherly.service.ScannerService;
import com.gogatherly.gogatherly.service.TicketInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.*;

@RestController
@RequestMapping(path = "/em")
public class EventManagerController {
    @Autowired
    private EventManagerService eventManagerService;

    @Autowired
    private TicketInstanceService ticketInstanceService;

    @Autowired
    private ScannerService scannerService;

    @Autowired
    private QrCodeService qrCodeService;
    @GetMapping(
            path = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<EventManagerResponse> getProfile(){
        EventManagerResponse res = eventManagerService.getProfile();

        return WebResponse
                .<EventManagerResponse>builder()
                .status("success")
                .message("success get profile")
                .data(res)
                .build();
    }

    @PatchMapping(
            path = "/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventManagerResponse> updateProfile(@ModelAttribute EventManagerUpdateRequest request){
        EventManagerResponse response = eventManagerService.updateProfile(request);

        return WebResponse
                .<EventManagerResponse>builder()
                .status("success")
                .message("success update profile")
                .data(response)
                .build();
    }

    @PostMapping(
            path = "/event/{eventId}/scanTicket",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Map<String, String>> scanTicket(@RequestBody VerifyQrCodeRequest request, @PathVariable("eventId") Integer eventId){
        TicketInstance ticket = qrCodeService.scanQrcode(request, eventId);
        Map<String,String> response = new HashMap<>();
        response.put("ticket_id",ticket.getId());
        response.put("buyer_ticket", ticket.getUser().getName());
        return WebResponse
                .<Map<String, String>>builder()
                .status("success")
                .message("ticket success register")
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/event/{eventId}/ticketInstance",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseList<List<TicketInstanceResponse>> getTicketInstance(
            @PathVariable("eventId") Integer eventId,
            @RequestParam(name = "page",required = false, defaultValue = "0" ) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ){
        WebResponseList<List<TicketInstanceResponse>> response = ticketInstanceService.getAllTciketInstance(page, size, eventId);
        return response;
    }

    @PostMapping(
            path="/event/{eventId}/scanner",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<Map<String, Object>>> createScanner(@PathVariable("eventId") Integer eventId, @RequestBody ScannerRequest request){
        Scanner scanner = scannerService.createScanner(request, eventId);

        LinkedHashMap<String,Object> response = new LinkedHashMap<>();
        response.put("username", scanner.getUsername());
        response.put("id", scanner.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WebResponse
                        .<Map<String, Object>>builder()
                        .status("success")
                        .message("success create scanner for ticket")
                        .data(response)
                        .build());
    }

    @PatchMapping(
            path = "/event/{eventId}/scanner/{scannerId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Map<String , Object>> changePasswordScanner(@PathVariable("eventId") Integer eventId,@PathVariable("scannerId") Integer scannerId, @RequestBody ScannerUdateRequest request){
        Scanner scanner = scannerService.changePassword(request, scannerId, eventId);
        LinkedHashMap<String,Object> response = new LinkedHashMap<>();
        response.put("username", scanner.getUsername());
        response.put("id", scanner.getId());

        return WebResponse
                        .<Map<String, Object>>builder()
                        .status("success")
                        .message("success update password scanner")
                        .data(response)
                        .build();
    }

    @DeleteMapping(
            path = "/event/{eventId}/scanner/{scannerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Map<String, Object>> deleteScanner(@PathVariable("eventId") Integer eventId, @PathVariable("scannerId") Integer scannerId){
        Scanner scanner = scannerService.deletedScanner(eventId, scannerId);
        LinkedHashMap<String,Object> response = new LinkedHashMap<>();
        response.put("username", scanner.getUsername());
        response.put("id", scanner.getId());

        return WebResponse
                .<Map<String, Object>>builder()
                .status("success")
                .message("success delet scanner with id "+scannerId)
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/event/{eventId}/scanner",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<Map<String, Object>>> getAllScanner(@PathVariable("eventId") Integer eventId){
        List<Scanner> scanners = scannerService.getAllScanner(eventId);
        LinkedList<Map<String,Object>> responses = new LinkedList<>();
        for(Scanner scanner : scanners){
            LinkedHashMap<String,Object> response = new LinkedHashMap<>();
            response.put("username", scanner.getUsername());
            response.put("id", scanner.getId());
            responses.add(response);
        }
        return WebResponse
                .<List<Map<String, Object>>>builder()
                .status("success")
                .message("get all scanner")
                .data(responses)
                .build();
    }

}


