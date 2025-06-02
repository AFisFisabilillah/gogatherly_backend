package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventManagerResponse {
    private Integer id;
    private String name;
    private String email;
}
