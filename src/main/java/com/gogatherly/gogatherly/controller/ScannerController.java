package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.ScannerLoginRequest;
import com.gogatherly.gogatherly.dto.ScannerRequest;
import com.gogatherly.gogatherly.dto.VerifyQrCodeRequest;
import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.model.entity.Scanner;
import com.gogatherly.gogatherly.model.entity.TicketInstance;
import com.gogatherly.gogatherly.service.QrCodeService;
import com.gogatherly.gogatherly.service.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/scanner")
public class ScannerController {
    @Autowired
    private ScannerService scannerService;
    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping(
            path = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Map<String,Object>> login(@RequestBody ScannerLoginRequest request){
        Map<String, Object> responses = scannerService.loginScanner(request);
        return WebResponse
                .<Map<String, Object>>builder()
                .data(responses)
                .message("success login scanner")
                .status("success")
                .build();
    }

    @PostMapping(
            path = "/scan",
            produces =  MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<Map<String, String>> scan(@RequestBody VerifyQrCodeRequest request){

        Scanner scanner = (Scanner) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TicketInstance ticket = qrCodeService.scanQrCodeScanner(request, scanner.getEvent());

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
}
