package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketInstanceResponse {
    private String ticketId;
    private String orderId;
    private String user;
    private String nik;
    private String phoneNumber;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private String ticketType;
}
