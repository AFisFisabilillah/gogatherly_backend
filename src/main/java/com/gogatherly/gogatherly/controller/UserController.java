package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.dto.*;
import com.gogatherly.gogatherly.model.entity.EventManager;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.EventManagerRepository;
import com.gogatherly.gogatherly.service.EventManagerService;
import com.gogatherly.gogatherly.service.EventService;
import com.gogatherly.gogatherly.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.InetAddress;
import java.text.Format;
import java.util.*;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventManagerService eventManagerService;
    @Autowired
    private EventManagerRepository eventManagerRepository;


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
        response.setName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setPhotoProfile("/public/profilePhoto/"+user.getProfilePhoto());
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

    @PatchMapping(
            path = "/users/profile",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )

    public WebResponse<UserResponse> updateProfile(@ModelAttribute UserUpdateRequest request){

        UserResponse response = userService.updateProfile(request);
        return WebResponse
                .<UserResponse>builder()
                .status("success")
                .message("success update your profile")
                .data(response)
                .build();

    }

    @GetMapping(
            path = "/event-manager/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventManagerDetailResponse> getEventManagerByID(@PathVariable("id")Integer id){
        EventManagerDetailResponse res = eventManagerService.getEventManagerByid(id);



        return WebResponse
                .<EventManagerDetailResponse>builder()
                .data(res)
                .status("success")
                .message("success get event manager with id "+id)
                .build();
    }

    @GetMapping(
            path = "/event-manager",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public  WebResponseList<List<EventManagerResponse>> getEventManager(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ){
        Page<EventManager> eventManagers = eventManagerService.getEventManager(page, size);

        List<EventManagerResponse> responses = new LinkedList<>();

        for(EventManager eventManager : eventManagers.getContent()){

            EventManagerResponse response = new EventManagerResponse();

            response.setName(eventManager.getName());
            response.setPhotoProfile("/public/profilePhoto/"+eventManager.getProfilePhoto());
            response.setEmail(eventManager.getEmail());
            response.setId(eventManager.getId());
            response.setDescription(eventManager.getDescription());
            log.info(response.getName());
            responses.add(response);
        }

        MetaData metaData = new MetaData();
        metaData.setPage(eventManagers.getNumber());
        metaData.setSize(eventManagers.getSize());
        metaData.setTotalPages(eventManagers.getTotalPages());
        metaData.setTotalElements(eventManagers.getTotalElements());
        metaData.setHasNext(eventManagers.hasNext());
        metaData.setHasPrevious(eventManagers.hasPrevious());

        return WebResponseList
                .<List<EventManagerResponse>>builder()
                .status("success")
                .message("success get event manager")
                .data(responses)
                .meta(metaData)
                .build();
    }
}
