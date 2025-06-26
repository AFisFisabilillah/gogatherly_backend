package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.ScannerLoginRequest;
import com.gogatherly.gogatherly.dto.ScannerRequest;
import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.model.entity.Scanner;
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


}
