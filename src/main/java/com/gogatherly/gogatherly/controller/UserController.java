package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.*;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.service.EventService;
import com.gogatherly.gogatherly.service.UserService;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;




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

    @PatchMapping(
            path = "/users/profile/photo",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public WebResponse<UserResponse> updatePhotoProfile(@RequestPart(name = "photo") MultipartFile file){
        User user = userService.changePhotoProfile(file);
        UserResponse response = new UserResponse();
        response.setName(user.getEmail());
        response.setNik(user.getNik());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setPhotoProfile(user.getProfilePhoto());
        response.setEmail(user.getEmail());

        return WebResponse
                .<UserResponse>builder()
                .status("success")
                .message("success change profilr photo")
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/users/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getProfile(){
        UserResponse profile = userService.getProfile();

        return WebResponse
                .<UserResponse>builder()
                .data(profile)
                .message("success get profile")
                .status("success")
                .build();
    }






}
