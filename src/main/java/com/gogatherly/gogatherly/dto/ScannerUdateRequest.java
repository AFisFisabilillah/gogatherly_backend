package com.gogatherly.gogatherly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScannerUdateRequest {
    @NotBlank
    @Size(max = 200, min = 6)
    private String newPassword;
}
