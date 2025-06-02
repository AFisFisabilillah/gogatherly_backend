package com.gogatherly.gogatherly.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
public class ErrorResponseException extends RuntimeException {
    private String message;
    private String status;
    private HttpStatus statusCode;
    private Map<String, String> data ;

    public ErrorResponseException(HttpStatus statusCode , String status, String message) {
        super(message);
        this.statusCode = statusCode;
        this.status=status;
        this.message = message;
    }

    public ErrorResponseException(HttpStatus statusCode , String status, String message, Map<String,String> data) {
        super(message);
        this.statusCode = statusCode;
        this.status=status;
        this.message = message;
        this.data = data;
    }
}
