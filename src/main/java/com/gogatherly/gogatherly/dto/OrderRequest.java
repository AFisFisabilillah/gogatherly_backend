package com.gogatherly.gogatherly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotBlank
    private String  eventId;

    @NotBlank
    private String ticketId;

    @NotNull
    private Long quantity;
}
