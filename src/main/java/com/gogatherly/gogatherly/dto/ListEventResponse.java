package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListEventResponse {
    private Integer id;

    private String title;

    private String baner;

    private LocationEventResponse location;

    private String endDate;

    private String startDate;

    private String endTime;

    private String startTime;

    private EventManagerResponse eventManager;
}
