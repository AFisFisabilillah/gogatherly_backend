package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.EventManagerResponse;
import com.gogatherly.gogatherly.dto.EventManagerUpdateRequest;
import com.gogatherly.gogatherly.dto.VerifyQrCodeRequest;
import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.model.entity.TicketInstance;
import com.gogatherly.gogatherly.service.EventManagerService;
import com.gogatherly.gogatherly.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/em")
public class EventManagerController {
    @Autowired
    private EventManagerService eventManagerService;
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
}


