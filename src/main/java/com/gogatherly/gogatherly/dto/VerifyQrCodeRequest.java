package com.gogatherly.gogatherly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyQrCodeRequest {
    @NotBlank
    private String ticketId;

    @NotBlank
    @Pattern(regexp = "^\\d{6}([04][1-9]|[1256][0-9]|[37][01])(0[1-9]|1[0-2])\\d{2}\\d{4}$", message = "format nik wrong")
    private String nik;
}
