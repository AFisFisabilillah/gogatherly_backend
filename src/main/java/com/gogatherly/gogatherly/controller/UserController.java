package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.EventResponse;
import com.gogatherly.gogatherly.dto.ListEventResponse;
import com.gogatherly.gogatherly.dto.WebResponse;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.service.EventService;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private EventService eventService;
    @GetMapping(
            path = "/users/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, String> profile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String principal = (String) authentication.getPrincipal();

        return new HashMap<>(Map.of("name", principal));
    }


    @GetMapping(
            path = "/events/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ListEventResponse>> eventSearching(
            @RequestParam(value = "q", required = false, defaultValue = " ") String q,
            @RequestParam(value = "category", required = false, defaultValue = " ") String category,
            @RequestParam(value = "date", required = false, defaultValue = " ") String date
    ){
        List<ListEventResponse> responses = eventService.searchEventUser(date, category, q);

        return WebResponse
                .<List<ListEventResponse>>builder()
                .status("success")
                .message("get event serach")
                .data(responses)
                .build();
    }

    @GetMapping(
            path = "/events/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventResponse> getDetailEvent(@PathVariable("id") Integer id){
        EventResponse response = eventService.getEventByid(id);
        return WebResponse
                .<EventResponse>builder()
                .status("success")
                .message("get event with id "+id)
                .data(response)
                .build();
    }




}
