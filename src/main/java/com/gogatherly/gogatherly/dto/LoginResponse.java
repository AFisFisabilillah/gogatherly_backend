package com.gogatherly.gogatherly.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
public class LoginResponse {
    private Integer id;

    private String name;

    private String token;

}
