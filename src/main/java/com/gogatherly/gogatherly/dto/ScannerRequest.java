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
public class ScannerRequest {
    @NotBlank
    @Size(max = 200, min = 3)
    @Unique(entity = Scanner.class, column = "username", message = "username exsist")
    @Pattern(regexp = "^[^\\s]+$", message = "no whitespace")
    private String username;

    @NotBlank
    @Size(max = 200, min = 6)
    private String password;

}
