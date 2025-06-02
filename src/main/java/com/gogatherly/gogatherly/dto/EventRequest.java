package com.gogatherly.gogatherly.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequest {
    @NotBlank(message = "title not blank")
    @Size(min = 0, max = 200, message = "title must min 0 character and max 200 character")
    private String title;


    @NotNull(message = "location must not null")
    @Valid
    private LocationEventRequest location;

    @NotNull(message = "caet must not null")
    @Valid
    private List<@Valid CategoriesRequest> categories;

    @NotBlank(message = "descriptiom not blank")
    private String description;

    @NotBlank(message = "violation not blank")
    private String violation;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    @NotNull
    @Valid
    private List<@Valid TicketRequest> tickets;

}
