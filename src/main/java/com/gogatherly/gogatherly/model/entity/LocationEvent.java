package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;


@Getter
@Setter
@Embeddable
public class LocationEvent {
    private String city;
    private String province;
    private String addres;
    private Double latitude;
    private Double longitude;
}
