package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private String id;

    private String title;

    private Long price;

    private Long quantity;

    private String description;

    private LocalDateTime startTime;

    private Boolean isFree;

    private LocalDateTime endTime;

}
