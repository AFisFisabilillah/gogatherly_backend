package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.*;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.service.AuthenticateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticateController {
    @Autowired
    private AuthenticateService authenticateService;
    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<Map<String, String>>> register(@RequestBody RegisterRequest request){
        User user = authenticateService.registerUser(request);

        Map<String , String> userInfo = new HashMap<>();
        userInfo.put("email",user.getEmail());
        userInfo.put("name", user.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WebResponse.<Map<String, String>>builder()
                        .status("success")
                        .message("register berhasil, emal sdah dikirman ke mail "+user.getEmail())
                        .data(userInfo)
                        .build());
    }
    @PostMapping(
            path = "/register/eventManager",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<Map<String, String>>> registerEventManager(@RequestBody RegisterRequest request){
        User user = authenticateService.registerEventManager(request);

        Map<String , String> userInfo = new HashMap<>();
        userInfo.put("email",user.getEmail());
        userInfo.put("name", user.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WebResponse.<Map<String, String>>builder()
                        .status("success")
                        .message("register berhasil, emal sdah dikirman ke mail "+user.getEmail())
                        .data(userInfo)
                        .build());
    }

    @PostMapping(
            path = "/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> verificationCode(@RequestBody VerificationCodeRequest request){
        authenticateService.verifyCode(request);
        return WebResponse
                .<String>builder()
                .status("success")
                .message("verifikasi berhasil")
                .build();
    }

    @PatchMapping(
            path = "/resend",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> resendEmailVerification(@RequestBody ResendEmailVerificationRequest request){
        authenticateService.resendEmailVerification(request);
        return WebResponse.
                <String>builder()
                .status("success")
                .message("berhasil mengirim ualng kode verifikasi")
                .build();
    }

    @PatchMapping(
            path = "/changeEmail",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> changeEmail(@RequestBody ChangeEmailVerificationRequest request){

        authenticateService.changeEmail(request);
        return WebResponse
                .<String>builder()
                .status("success")
                .message("success change email and sent email verification to your new email ")
                .build();
    }

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<LoginResponse> login(@RequestBody LoginRequest request){
        log.info("Masuk ke controlle auht dengan login");
        LoginResponse res = authenticateService.login(request);
        return WebResponse
                .<LoginResponse>builder()
                .status("success")
                .message("login success")
                .data(res)
                .build();
    }






}
