package com.gogatherly.gogatherly.dto;

import com.gogatherly.gogatherly.model.entity.Scanner;
import com.gogatherly.gogatherly.validation.annotation.Unique;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScannerLoginRequest {
    @NotBlank
    @Pattern(regexp = "^[^\\s]+$", message = "no whitespace")
    private String username;

    @NotBlank
    private String password;
}
