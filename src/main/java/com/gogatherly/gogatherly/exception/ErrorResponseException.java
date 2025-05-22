package com.gogatherly.gogatherly.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponseException extends RuntimeException {
    private String message;
    private String status;
    private HttpStatus statusCode;
    private String data = null;

    public ErrorResponseException(HttpStatus statusCode , String status, String message) {
        super(message);
        this.statusCode = statusCode;
        this.status=status;
        this.message = message;
    }

    public ErrorResponseException(HttpStatus statusCode , String status, String message, String data) {
        super(message);
        this.statusCode = statusCode;
        this.status=status;
        this.message = message;
        this.data = data;
    }
}
