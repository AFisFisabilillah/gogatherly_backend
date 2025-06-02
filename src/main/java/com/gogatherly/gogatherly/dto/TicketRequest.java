package com.gogatherly.gogatherly.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotBlank(message = "title ticket not blank")
    @Size(min = 0, max = 200, message = "title ticket must min 0 character and max 200 character")
    private String title;

    @NotNull(message = "price ticket not blank")
    @Min(value = 0, message = "price must grater than 0 ")
    private Integer price;

    @NotNull(message = "quntity ticket not blank")
    @Min(value = 0, message = "quantity must grater than 0 ")
    private Integer quantity;

    @NotBlank(message = "description ticket not blank")
    private String description;

    @NotNull(message = "quntity ticket not blank")
    @Future
    private LocalDateTime startTime;

    @NotNull
    private Boolean isFree;

    @NotNull
    @Future
    private LocalDateTime endTime;

}
