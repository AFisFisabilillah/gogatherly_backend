package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LocationEventResponse {
    private String city;
    private String province;
    private String addres;
    private Double latitude;
    private  Double longitude;
}
