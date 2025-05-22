package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<Map<String, String>>> requestError(ConstraintViolationException exception){
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        Map<String, String> error = new HashMap<>();

        for (ConstraintViolation<?> violation : violations){
            error.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        WebResponse<Map<String, String>> response = WebResponse.<Map<String, String>>builder().status("error").message("validasi error").data(error).build();


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<WebResponse<?>> errorResponse(ErrorResponseException error){
            WebResponse<String> response = WebResponse
                    .<String>builder()
                    .status(error.getStatus())
                    .data(error.getData())
                    .message(error.getMessage())
                    .build();

            return ResponseEntity.status(error.getStatusCode()).body(response);

    }
}
