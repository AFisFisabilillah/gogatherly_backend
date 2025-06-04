package com.gogatherly.gogatherly.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor


public class UserResponse {
    private String name;
    private String nik;
    private String email;
    private String phoneNumber;
    private String photoProfile;
}
