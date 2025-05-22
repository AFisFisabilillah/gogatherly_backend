package com.gogatherly.gogatherly.dto;

import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.validation.annotation.Unique;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    @Size(min = 0, max = 200)
    private String name;

    @Email
    @Unique(entity = User.class, column = "email", message = "email alredy exists")
    @NotBlank
    @Size(min = 0, max = 200)
    private String email;

    @Pattern(regexp = "^\\d{6}([04][1-9]|[1256][0-9]|[37][01])(0[1-9]|1[0-2])\\d{2}\\d{4}$", message = "format nik wrong")
    @Unique(entity = User.class, column = "nik", message = "nik alredy exist")
    @NotBlank
    @Size(min = 0, max = 16)
    private String nik;

    @Unique(entity = User.class, column = "phoneNumber", message = "phone number alredy exists")
    @NotBlank
    @Pattern(regexp = "^(\\+62|62|0)8[1-9][0-9]{6,9}$", message = "number phone not valid")
    @Size(min = 0, max = 14)
    private String phoneNumber;

    @NotBlank
    private String password;









}
