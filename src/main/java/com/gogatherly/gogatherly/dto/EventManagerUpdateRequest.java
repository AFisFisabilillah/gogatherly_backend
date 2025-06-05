package com.gogatherly.gogatherly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventManagerUpdateRequest {

    @NotBlank
    @Size(max = 200)
    private String name;


    private String description;

    private MultipartFile photo;
}
