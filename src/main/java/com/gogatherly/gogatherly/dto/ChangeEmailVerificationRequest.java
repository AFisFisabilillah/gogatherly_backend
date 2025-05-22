package com.gogatherly.gogatherly.dto;

import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.validation.annotation.Unique;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeEmailVerificationRequest {
    @Email
    @NotBlank
    @Unique(entity = User.class, column = "email", message = "this email is exist")
    private String newEmail;

    @Email
    @NotBlank
    private String oldEmail;
}
