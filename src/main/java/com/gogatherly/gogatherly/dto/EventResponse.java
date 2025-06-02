package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private Integer id;
    private String title;
    private String banner;
    private String description;
    private String violation;
    private List<String> categories;
    private LocationEventResponse location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<TicketResponse> tickets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
