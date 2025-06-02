package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.EventRequest;
import com.gogatherly.gogatherly.dto.EventResponse;
import com.gogatherly.gogatherly.dto.ListEventResponse;
import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/em/events")
public class EventController {

    @Autowired
    private EventService eventService;


    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    private ResponseEntity<WebResponse<Map<String,String>>> createEvent(@RequestPart("event") EventRequest request, @RequestPart("banner")MultipartFile banner ) throws IOException {
        if(banner.isEmpty()){
            throw  new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "banner not null");
        }
        log.info("masuk ke eventController");
        Event event = eventService.createEvent(request, banner);
        Map<String, String> response = new HashMap<>(Map.of("id_event", event.getId().toString(), "title", event.getTitle()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WebResponse
                        .<Map<String, String>>builder()
                        .status("success")
                        .message("success create event")
                        .data(response)
                        .build());
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventResponse> detailEvent(@PathVariable(name = "id") Integer id){
        EventResponse response = eventService.detailEvent(id);

        return WebResponse
                .<EventResponse>builder()
                .status("success")
                .message("get detail event")
                .data(response)
                .build();
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ListEventResponse>> getEvents(@RequestParam(value = "q", required = false) String q){
        List<ListEventResponse> listEvent = eventService.getListEvent(q);

        return WebResponse
                .<List<ListEventResponse>>builder()
                .status("success")
                .message("berhasil mendapatkan events")
                .data(listEvent)
                .build();
    }




}
