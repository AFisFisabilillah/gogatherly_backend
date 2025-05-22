package com.gogatherly.gogatherly.controller;

import com.gogatherly.gogatherly.model.entity.User;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @GetMapping(
            path = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, String> profile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String principal = (String) authentication.getPrincipal();

        return new HashMap<>(Map.of("name", principal));
    }
}
