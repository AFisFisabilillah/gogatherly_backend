package com.gogatherly.gogatherly.dto;

import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.validation.annotation.Unique;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Pattern(regexp = "^(\\+62|62|0)8[1-9][0-9]{6,9}$", message = "number phone not valid")
    @Size(min = 0, max = 14)
    private String numberPhone;

    private MultipartFile photoProfile;
}
