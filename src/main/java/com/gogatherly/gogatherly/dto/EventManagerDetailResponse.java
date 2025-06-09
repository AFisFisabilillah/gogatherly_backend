package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventManagerDetailResponse {
    private Integer id;
    private String name;
    private String email;
    private String description;
    private String photoProfile;
    private List<ListEventResponse> events;
}
