package com.gogatherly.gogatherly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationEventRequest {

    @NotBlank(message = "city not blank")
    @Size(min = 0, max = 200,message = "title must min 0 character and max 200 character")
    private String city;

    @NotBlank(message = "province not blank")
    @Size(min = 0, max = 200,message = "province must min 0 character and max 200 character")
    private String province;

    @NotBlank(message = "addres not blank")
    @Size(min = 0, max = 200,message = "addres must min 0 character and max 200 character")
    private String addres;

   @NotNull(message = "latitude not null")
    private Double latitude;

    @NotNull(message = "longitude not null")
    private  Double longitude;

}
