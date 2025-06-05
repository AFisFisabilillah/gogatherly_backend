package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.EventManagerResponse;
import com.gogatherly.gogatherly.dto.EventManagerUpdateRequest;
import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.service.EventManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping(path = "/em")
public class EventManagerController {
    @Autowired
    private EventManagerService eventManagerService;
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
}


