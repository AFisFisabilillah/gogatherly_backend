package com.gogatherly.gogatherly.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.UserRepository;
import com.gogatherly.gogatherly.service.JwtService;
import com.gogatherly.gogatherly.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtFilter implements Filter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;

        log.info("masuk ke jwt filter");
        String authorization = request.getHeader("Authorization");
        log.info("apakh null = " +String.valueOf(request.getHeader("Authorization")==null));
        if(authorization == null){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        if(!authorization.startsWith("Bearer ")){
            log.info("nggak ada header authorize");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = authorization.substring( 7);
        try {
            String email = jwtService.getEmail(token);
            UserDetails user = userService.loadUserByUsername(email);

            log.info("Masih aman di jwt filter");

            if(user != null && SecurityContextHolder.getContext().getAuthentication() == null){
                log.info("token valid");
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("user sudah terdaftar");
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (Exception e){
            log.info("eh ada error : "+e.getMessage());
            Map<String, String> res = new HashMap<>();
            res.put("status", "error");
            res.put("message", e.getMessage());
            String value = objectMapper.writeValueAsString(res);

            response.setStatus(400);
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(value);
        }

    }
}